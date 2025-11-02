package com.iafenvoy.origins.data.condition.builtin.damage.meta;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public record NotCondition(DamageCondition condition) implements DamageCondition {
    public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageCondition.CODEC.fieldOf("condition").forGetter(NotCondition::new)
    ).apply(i, NotCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return !this.condition.test(source, amount);
    }
}
