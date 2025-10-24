package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record EquippedItemActionAction(EquipmentSlot slot, ItemAction action) implements EntityAction {
    public static final MapCodec<EquippedItemActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(EquippedItemActionAction::slot),
            ItemAction.CODEC.fieldOf("action").forGetter(EquippedItemActionAction::action)
    ).apply(i, EquippedItemActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source instanceof LivingEntity living)
            this.action.accept(living.level(), source, living.getItemBySlot(this.slot));
    }
}
