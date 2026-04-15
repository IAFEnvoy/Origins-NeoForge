package com.iafenvoy.origins.data.condition.builtin.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record PowerCountCondition(Optional<EquipmentSlot> slot, Comparison comparison) implements ItemCondition {
    public static final MapCodec<PowerCountCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EquipmentSlot.CODEC.optionalFieldOf("slot").forGetter(PowerCountCondition::slot),
            Comparison.CODEC.forGetter(PowerCountCondition::comparison)
    ).apply(i, PowerCountCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        Multimap<EquipmentSlot, Holder<Power>> map = stack.getOrDefault(OriginsDataComponents.ITEM_POWERS, HashMultimap.create());
        return this.comparison.compare(this.slot.map(List::of).orElse(List.of(EquipmentSlot.values())).stream().map(map::get).mapToInt(Collection::size).sum());
    }
}
