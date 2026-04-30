package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.InventoryUtil;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record DropInventoryAction(Optional<Holder<Power>> power, EntityAction entityAction, ItemAction itemAction,
                                  ItemCondition itemCondition, IntList slot, boolean throwRandomly,
                                  boolean retainOwnership, int amount) implements EntityAction {
    public static final MapCodec<DropInventoryAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Power.CODEC.optionalFieldOf("power").forGetter(DropInventoryAction::power),
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
