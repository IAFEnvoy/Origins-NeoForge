package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.RecipeHelper;
import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.screen.badge.BadgeTooltipManager;
import com.iafenvoy.origins.screen.tooltip.CraftingRecipeTooltipComponent;
import com.iafenvoy.origins.util.wrapper.Mutable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
public final class OriginsRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OriginsEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerRenderTypes(FMLClientSetupEvent event){
        ItemBlockRenderTypes.setRenderLayer(OriginsBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerBadgeTooltips(FMLClientSetupEvent event) {
        BadgeTooltipManager.register(BuiltinBadges.CRAFTING_RECIPE.get(), (badge, power, font, widthLimit, delta) -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            List<ClientTooltipComponent> tooltips = new LinkedList<>();
            if (client.level == null || player == null) return tooltips;
            RegistryAccess access = client.level.registryAccess();
            @Nullable CraftingRecipe recipe = badge.fromPower() ? access.registryOrThrow(PowerRegistries.POWER_KEY).get(badge.recipe()) instanceof RecipeHelper helper ? helper.getRecipe() : null
                    : client.level.getRecipeManager().byKey(badge.recipe()).map(RecipeHolder::value).filter(CraftingRecipe.class::isInstance).map(CraftingRecipe.class::cast).orElse(null);
            if (recipe == null) return tooltips;
            int recipeWidth = recipe instanceof ShapedRecipe shapedRecipe ? shapedRecipe.getWidth() : 3;
            SlotAccess outputStackReference = Mutable.stack(recipe.getResultItem(access)).toSlotAccess();
            OriginDataHolder.get(player).streamActivePowers(ModifyCraftingPower.class)
                    .filter(p -> p.doesApply(player, badge.recipe(), outputStackReference.get()))
                    .findFirst()
                    .ifPresent(p -> p.getNewResult(player, outputStackReference));
            CraftingRecipeTooltipComponent recipeTooltip = new CraftingRecipeTooltipComponent(recipeWidth, peekInputs(recipe), outputStackReference.get());
            Consumer<Component> addLines = component -> font.split(component, widthLimit).stream().map(ClientTextTooltip::new).forEach(tooltips::add);
            badge.prefix().ifPresent(addLines);
            tooltips.add(recipeTooltip);
            badge.suffix().ifPresent(addLines);
            if (client.options.advancedItemTooltips)
                addLines.accept(Component.literal(badge.recipe().toString()).withStyle(ChatFormatting.DARK_GRAY));
            return tooltips;
        });
        BadgeTooltipManager.register(BuiltinBadges.KEYBIND.get(), (badge, power, font, widthLimit, delta) -> {
            KeyMapping key = KeyMapping.ALL.get(power instanceof Toggleable toggleable ? toggleable.getKey().key() : badge.key());
            return List.of(ClientTooltipComponent.create(Component.translatable(badge.text(), Component.literal("[").append(key.getKey().getDisplayName()).append("]")).getVisualOrderText()));
        });
        BadgeTooltipManager.register(BuiltinBadges.TOOLTIP.get(), (badge, power, font, widthLimit, delta) -> List.of(ClientTooltipComponent.create(badge.text().getVisualOrderText())));
    }

    private static NonNullList<ItemStack> peekInputs(CraftingRecipe recipe) {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        List<Ingredient> ingredients = recipe.getIngredients();
        int seed = Mth.floor(System.currentTimeMillis() / 1.5 * 1000);
        for (int index = 0; index < ingredients.size(); index++) {
            ItemStack[] stacks = ingredients.get(index).getItems();
            if (stacks.length > 0) inputs.set(index, stacks[seed % stacks.length]);
        }
        return inputs;
    }
}
