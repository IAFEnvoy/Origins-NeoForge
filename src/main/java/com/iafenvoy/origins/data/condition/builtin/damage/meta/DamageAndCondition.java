package com.iafenvoy.origins.data.condition.builtin.damage.meta;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DamageAndCondition(List<DamageCondition> conditions) implements DamageCondition {
    public static final MapCodec<DamageAndCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageCondition.CODEC.listOf().fieldOf("conditions").forGetter(DamageAndCondition::conditions)
    ).apply(i, DamageAndCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return this.conditions.stream().allMatch(x -> x.test(source, amount));
    }
}
