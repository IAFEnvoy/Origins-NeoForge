package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerReference;
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

public record HasPowerCondition(PowerReference power, Optional<EquipmentSlot> slot) implements ItemCondition {
    public static final MapCodec<HasPowerCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            PowerReference.CODEC.fieldOf("power").forGetter(HasPowerCondition::power),
            EquipmentSlot.CODEC.optionalFieldOf("slot").forGetter(HasPowerCondition::slot)
    ).apply(i, HasPowerCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        ItemPowersComponent component = stack.getOrDefault(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.EMPTY);
        return this.power.get().map(power ->
                this.slot.map(List::of).orElse(List.of(EquipmentSlot.values())).stream().anyMatch(x -> component.contains(x, power))
        ).orElse(false);
    }
}
