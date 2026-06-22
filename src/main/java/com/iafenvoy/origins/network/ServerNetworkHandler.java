package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyPlayerSpawnPower;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ChooseRandomOriginC2SPayload;
import com.iafenvoy.origins.network.payload.ConfirmOriginS2CPayload;
import com.iafenvoy.origins.network.payload.PowerToggleC2SPayload;
import com.iafenvoy.origins.network.payload.RecipeBadgeS2CPayload;
import com.iafenvoy.origins.network.payload.RequestRecipeBadgeC2SPayload;
import com.iafenvoy.origins.util.HolderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ServerNetworkHandler {
    static void onChooseOrigin(ChooseOriginC2SPayload packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        OriginDataHolder holder = OriginDataHolder.get(player);
        Holder<Layer> layer = packet.layer();
        if (holder.hasOriginInLayer(layer)) {
            Origins.LOGGER.warn("Player {} tried to choose origin for layer \"{}\" while having one already.", player.getName().getString(), HolderHelper.string(layer));
            return;
        }

        Optional<Holder<Origin>> optional = packet.origin();
        if (optional.isPresent()) {
            Holder<Origin> origin = optional.get();
            if (origin.value().unchoosable() || layer.value().collectOrigins(context.player()).noneMatch(origin::equals)) {
                Origins.LOGGER.warn("Player {} tried to choose unchoosable origin \"{}\" from layer \"{}\"!", player.getName().getString(), HolderHelper.string(origin), HolderHelper.string(layer));
                holder.clearOrigin(layer);
            } else {
                holder.setOrigin(layer, origin);
                if (packet.firstJoin()) teleportToModifiedSpawn(player);
                Origins.LOGGER.info("Player {} chose origin \"{}\" for layer \"{}\"", player.getName().getString(), HolderHelper.string(origin), HolderHelper.string(layer));
            }
        } else randomOrigin(player, holder, layer, packet.firstJoin());
        context.reply(new ConfirmOriginS2CPayload(layer, holder.getOrigin(layer)));
        holder.getData().setSelecting(false);
        holder.sync();
    }

    static void onChooseRandomOrigin(ChooseRandomOriginC2SPayload packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        OriginDataHolder holder = OriginDataHolder.get(player);
        Holder<Layer> layer = packet.layer();
        if (holder.hasOriginInLayer(layer)) {
            Origins.LOGGER.warn("Player {} tried to choose random origin for layer \"{}\" while having one already.", player.getName().getString(), HolderHelper.string(layer));
            return;
        }

        randomOrigin(player, holder, layer, false);
        context.reply(new ConfirmOriginS2CPayload(layer, holder.getOrigin(layer)));
        holder.getData().setSelecting(false);
        holder.sync();
    }

    private static void randomOrigin(ServerPlayer player, OriginDataHolder holder, Holder<Layer> layer, boolean firstJoin) {
        List<Holder<Origin>> randomOriginIds = layer.value().collectRandomizableOrigins(player).toList();
        if (!layer.value().allowRandom() || randomOriginIds.isEmpty()) {
            Origins.LOGGER.warn("Player {} tried to choose a random origin for layer \"{}\", which is not allowed!", player.getName().getString(), HolderHelper.string(layer));
            holder.clearOrigin(layer);
        } else {
            Holder<Origin> origin = randomOriginIds.get(player.getRandom().nextInt(randomOriginIds.size()));
            holder.setOrigin(layer, origin);
            if (firstJoin) teleportToModifiedSpawn(player);
            Origins.LOGGER.info("Player {} was randomly assigned the following origin: {}", player.getName().getString(), HolderHelper.string(origin));
        }
    }

    private static void teleportToModifiedSpawn(ServerPlayer player) {
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.streamActivePowers(ModifyPlayerSpawnPower.class)
            .findFirst()
            .flatMap(power -> power.getSpawn(player))
            .ifPresent(spawn -> {
                ServerLevel targetLevel = player.level().getServer().getLevel(spawn.getA().dimension());
                if (targetLevel != null) {
                    BlockPos pos = spawn.getB();
                    player.teleportTo(targetLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
                }
            });
    }

    public static void onPowerToggle(PowerToggleC2SPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.getHelper().toggle(payload.key());
    }

    public static void onRequestRecipeBadge(RequestRecipeBadgeC2SPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        CraftingRecipe recipe = player.level().getServer().getRecipeManager()
                .byKey(ResourceKey.create(Registries.RECIPE, payload.recipe()))
                .map(RecipeHolder::value)
                .filter(CraftingRecipe.class::isInstance)
                .map(CraftingRecipe.class::cast)
                .orElse(null);
        if (recipe == null) {
            context.reply(new RecipeBadgeS2CPayload(payload.recipe(), 0, List.of(), ItemStack.EMPTY));
            return;
        }

        int width = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
        int height = recipe instanceof ShapedRecipe shaped ? shaped.getHeight() : 3;
        NonNullList<ItemStack> inputs = sampleRecipeInputs(recipe);
        ItemStack result = recipe.assemble(CraftingInput.of(width, height, inputs.subList(0, width * height)));
        context.reply(new RecipeBadgeS2CPayload(payload.recipe(), width, inputs, result));
    }

    @SuppressWarnings("deprecation")
    private static NonNullList<ItemStack> sampleRecipeInputs(CraftingRecipe recipe) {
        NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        PlacementInfo placement = recipe.placementInfo();
        List<Ingredient> ingredients = placement.ingredients();
        for (int slot = 0; slot < Math.min(9, placement.slotsToIngredientIndex().size()); slot++) {
            int ingredientIndex = placement.slotsToIngredientIndex().getInt(slot);
            if (ingredientIndex < 0 || ingredientIndex >= ingredients.size()) continue;
            Holder<net.minecraft.world.item.Item> item = ingredients.get(ingredientIndex).items().findFirst().orElse(null);
            if (item != null) inputs.set(slot, new ItemStack(item));
        }
        return inputs;
    }
}
