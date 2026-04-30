package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.iafenvoy.origins.util.InventoryUtil;
import com.iafenvoy.origins.util.Mutable;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public record DropInventoryAction(Optional<ResourceLocation> power, EntityAction entityAction, ItemAction itemAction,
                                  ItemCondition itemCondition, IntList slot, boolean throwRandomly,
                                  boolean retainOwnership, int amount) implements EntityAction {
    public static final MapCodec<DropInventoryAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("power").forGetter(DropInventoryAction::power),
            EntityAction.optionalCodec("entity_action").forGetter(DropInventoryAction::entityAction),
            ItemAction.optionalCodec("item_action").forGetter(DropInventoryAction::itemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(DropInventoryAction::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(DropInventoryAction::slot),
            Codec.BOOL.optionalFieldOf("throw_randomly", false).forGetter(DropInventoryAction::throwRandomly),
            Codec.BOOL.optionalFieldOf("retain_ownership", true).forGetter(DropInventoryAction::retainOwnership),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(DropInventoryAction::amount)
    ).apply(instance, DropInventoryAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        InventoryUtil.dropInventory(this.slot, this.entityAction, this.itemCondition, this.itemAction, this.throwRandomly, this.retainOwnership, source, this.power, this.amount);
    }
}
