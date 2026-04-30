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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ReplaceInventoryAction(EntityAction entityAction, ItemAction itemAction, ItemCondition itemCondition,
                                     IntList slot, Optional<Holder<Power>> power, ItemStack stack,
                                     boolean mergeComponent) implements EntityAction {
    public static final MapCodec<ReplaceInventoryAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action").forGetter(ReplaceInventoryAction::entityAction),
            ItemAction.CODEC.fieldOf("item_action").forGetter(ReplaceInventoryAction::itemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(ReplaceInventoryAction::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(ReplaceInventoryAction::slot),
            Power.CODEC.optionalFieldOf("power").forGetter(ReplaceInventoryAction::power),
            ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(ReplaceInventoryAction::stack),
            Codec.BOOL.optionalFieldOf("merge_component", false).forGetter(ReplaceInventoryAction::mergeComponent)
    ).apply(i, ReplaceInventoryAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        InventoryUtil.replaceInventory(this.slot, this.stack, this.entityAction, this.itemCondition, this.itemAction, source, this.power, this.mergeComponent);
    }
}
