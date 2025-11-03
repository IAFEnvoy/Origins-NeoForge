package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DurabilityCondition(Comparison comparison, int compareTo) implements ItemCondition {
    public static final MapCodec<DurabilityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(DurabilityCondition::comparison),
            Codec.INT.fieldOf("compare_to").forGetter(DurabilityCondition::compareTo)
    ).apply(i, DurabilityCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return stack.isDamageableItem() && this.comparison.compare(Math.abs(stack.getMaxDamage() - stack.getDamageValue()), this.compareTo);
    }
}
