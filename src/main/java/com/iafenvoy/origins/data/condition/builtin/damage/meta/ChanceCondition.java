package com.iafenvoy.origins.data.condition.builtin.damage.meta;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public record ChanceCondition(double chance) implements DamageCondition {
    public static final MapCodec<ChanceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(ChanceCondition::chance)
    ).apply(i, ChanceCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return Math.random() < this.chance;
    }
}
