package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.RecipeHelper;
import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.network.payload.RecipeBadgeS2CPayload;
import com.iafenvoy.origins.network.payload.RequestRecipeBadgeC2SPayload;
import com.iafenvoy.origins.screen.badge.BadgeTooltipManager;
import com.iafenvoy.origins.screen.tooltip.CraftingRecipeTooltipComponent;
import com.iafenvoy.origins.util.wrapper.Mutable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
public final class OriginsRenderers {
    private static final Map<Identifier, RecipeBadgeS2CPayload> RECIPE_BADGES = new HashMap<>();
    private static final Set<Identifier> REQUESTED_RECIPE_BADGES = new HashSet<>();

    private OriginsRenderers() {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OriginsEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerBadgeTooltips(FMLClientSetupEvent event) {
        BadgeTooltipManager.register(BuiltinBadges.CRAFTING_RECIPE.get(), (badge, power, font, widthLimit, delta) -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            List<ClientTooltipComponent> tooltips = new LinkedList<>();
            if (client.level == null || player == null) return tooltips;

            int recipeWidth;
            NonNullList<ItemStack> inputs;
            ItemStack originalResult;
            if (badge.fromPower()) {
                RegistryAccess access = client.level.registryAccess();
                CraftingRecipe recipe = PowerReference.listAllPowers(access)
                        .filter(holder -> Objects.equals(holder.id(), badge.recipe()))
                        .map(PowerHolder::power)
                        .filter(RecipeHelper.class::isInstance)
                        .map(RecipeHelper.class::cast)
                        .map(RecipeHelper::getRecipe)
                        .findAny()
                        .orElse(null);
                if (recipe == null) return tooltips;
                recipeWidth = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
                int recipeHeight = recipe instanceof ShapedRecipe shaped ? shaped.getHeight() : 3;
                inputs = peekInputs(recipe);
                originalResult = recipe.assemble(CraftingInput.of(
                        recipeWidth, recipeHeight, inputs.subList(0, recipeWidth * recipeHeight)
                ));
            } else {
                RecipeBadgeS2CPayload preview = RECIPE_BADGES.get(badge.recipe());
                if (preview == null) {
                    if (REQUESTED_RECIPE_BADGES.add(badge.recipe()))
                        ClientPacketDistributor.sendToServer(new RequestRecipeBadgeC2SPayload(badge.recipe()));
                    return tooltips;
                }
                if (preview.width() <= 0 || preview.result().isEmpty()) return tooltips;
                recipeWidth = preview.width();
                inputs = NonNullList.withSize(9, ItemStack.EMPTY);
                for (int i = 0; i < Math.min(inputs.size(), preview.inputs().size()); i++)
                    inputs.set(i, preview.inputs().get(i).copy());
                originalResult = preview.result().copy();
            }
            SlotAccess output = Mutable.stack(originalResult).toSlotAccess();
            OriginDataHolder.get(player).streamActivePowers(ModifyCraftingPower.class)
                    .filter(p -> p.doesApply(player, badge.recipe(), output.get()))
                    .findFirst()
                    .ifPresent(p -> p.getNewResult(player, output));

            Consumer<Component> addLines = component -> font.split(component, widthLimit)
                    .stream()
                    .map(ClientTextTooltip::new)
                    .forEach(tooltips::add);
            badge.prefix().ifPresent(addLines);
            tooltips.add(new CraftingRecipeTooltipComponent(recipeWidth, inputs, output.get()));
            badge.suffix().ifPresent(addLines);
            if (client.options.advancedItemTooltips)
                addLines.accept(Component.literal(badge.recipe().toString()).withStyle(ChatFormatting.DARK_GRAY));
            return tooltips;
        });

        BadgeTooltipManager.register(BuiltinBadges.KEYBIND.get(), (badge, power, font, widthLimit, delta) -> {
            String mappingName = power instanceof Toggleable toggleable ? toggleable.getKey().key() : badge.key();
            KeyMapping key = KeyMapping.ALL.get(mappingName);
            Component keyName = key == null ? Component.literal(mappingName) : key.getKey().getDisplayName();
            return List.of(ClientTooltipComponent.create(Component.translatable(
                    badge.text(), Component.literal("[").append(keyName).append("]")
            ).getVisualOrderText()));
        });
        BadgeTooltipManager.register(BuiltinBadges.TOOLTIP.get(), (badge, power, font, widthLimit, delta) ->
                List.of(ClientTooltipComponent.create(badge.text().getVisualOrderText())));
    }

    public static void receiveRecipeBadge(RecipeBadgeS2CPayload payload) {
        RECIPE_BADGES.put(payload.recipe(), payload);
    }

    @SubscribeEvent
    public static void clearRecipeBadges(ClientPlayerNetworkEvent.LoggingOut event) {
        RECIPE_BADGES.clear();
        REQUESTED_RECIPE_BADGES.clear();
    }

    @SuppressWarnings("deprecation")
    private static NonNullList<ItemStack> peekInputs(CraftingRecipe recipe) {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        PlacementInfo placement = recipe.placementInfo();
        List<Ingredient> ingredients = placement.ingredients();
        int seed = Mth.floor(System.currentTimeMillis() / 1500.0);

        for (int slot = 0; slot < Math.min(9, placement.slotsToIngredientIndex().size()); slot++) {
            int ingredientIndex = placement.slotsToIngredientIndex().getInt(slot);
            if (ingredientIndex < 0 || ingredientIndex >= ingredients.size()) continue;
            List<ItemStack> choices = ingredients.get(ingredientIndex).items().map(ItemStack::new).toList();
            if (!choices.isEmpty()) inputs.set(slot, choices.get(Math.floorMod(seed, choices.size())));
        }
        return inputs;
    }
}
