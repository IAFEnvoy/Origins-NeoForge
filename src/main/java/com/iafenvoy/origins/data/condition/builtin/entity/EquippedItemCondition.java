package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

public record EquippedItemCondition(EquipmentSlot equipmentSlot,
                                    ItemCondition itemCondition) implements EntityCondition {
    public static final MapCodec<EquippedItemCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EquipmentSlot.CODEC.fieldOf("equipment_slot").forGetter(EquippedItemCondition::equipmentSlot),
            ItemCondition.optionalCodec("item_condition").forGetter(EquippedItemCondition::itemCondition)
    ).apply(i, EquippedItemCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        var stack = living.getItemBySlot(this.equipmentSlot);
        boolean hasItem = !stack.isEmpty() && this.itemCondition.test(entity.level(), stack);
        return hasItem;
    }
}
