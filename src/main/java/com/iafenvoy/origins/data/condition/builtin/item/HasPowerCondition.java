package com.iafenvoy.origins.data.condition.builtin.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record HasPowerCondition(Holder<Power> power, Optional<EquipmentSlot> slot) implements ItemCondition {
    public static final MapCodec<HasPowerCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Power.CODEC.fieldOf("power").forGetter(HasPowerCondition::power),
            EquipmentSlot.CODEC.optionalFieldOf("slot").forGetter(HasPowerCondition::slot)
    ).apply(i, HasPowerCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        Multimap<EquipmentSlot, Holder<Power>> map = stack.getOrDefault(OriginsDataComponents.ITEM_POWERS, HashMultimap.create());
        return this.slot.map(List::of).orElse(List.of(EquipmentSlot.values())).stream().anyMatch(x -> map.containsEntry(x, this.power));
    }
}
