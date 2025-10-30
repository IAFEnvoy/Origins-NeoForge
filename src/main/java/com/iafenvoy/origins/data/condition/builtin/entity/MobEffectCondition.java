package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record MobEffectCondition(Holder<MobEffect> effect, int minAmplifier, int maxAmplifier, int minDuration,
                                 int maxDuration) implements EntityCondition {
    public static final MapCodec<MobEffectCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectCondition::effect),
            Codec.INT.optionalFieldOf("min_amplifier", 0).forGetter(MobEffectCondition::minAmplifier),
            Codec.INT.optionalFieldOf("max_amplifier", Integer.MAX_VALUE).forGetter(MobEffectCondition::maxAmplifier),
            Codec.INT.optionalFieldOf("min_duration", -1).forGetter(MobEffectCondition::minDuration),
            Codec.INT.optionalFieldOf("max_duration", Integer.MAX_VALUE).forGetter(MobEffectCondition::maxDuration)
    ).apply(i, MobEffectCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        MobEffectInstance instance = living.getEffect(this.effect);
        return instance != null
                && this.minAmplifier <= instance.getAmplifier() && instance.getAmplifier() <= this.maxAmplifier
                && this.minDuration <= instance.getDuration() && instance.getDuration() <= this.maxDuration;
    }
}
