package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DamageOverTimePower implements Power {
    public static final MapCodec<DamageOverTimePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("interval", 20).forGetter(DamageOverTimePower::getInterval),
            Codec.INT.optionalFieldOf("onset_delay").forGetter(DamageOverTimePower::getOnSetDelay),
            Codec.FLOAT.fieldOf("damage").forGetter(DamageOverTimePower::getDamage),
            Codec.FLOAT.optionalFieldOf("damage_easy").forGetter(DamageOverTimePower::getDamageEasy),
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageOverTimePower::getDamageType)
    ).apply(i, DamageOverTimePower::new));

    private final int interval;
    private final Optional<Integer> onSetDelay;
    private final float damage, damageEasy;
    private final Holder<DamageType> damageType;
    private int remainTicks;

    public DamageOverTimePower(int interval, Optional<Integer> onSetDelay, float damage, Optional<Float> damageEasy, Holder<DamageType> damageType) {
        this.interval = interval;
        this.onSetDelay = onSetDelay;
        this.remainTicks = this.onSetDelay.orElse(0);
        this.damage = damage;
        this.damageEasy = damageEasy.orElse(this.damage);
        this.damageType = damageType;
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if (this.remainTicks <= 0) {
            this.remainTicks = this.interval;
            entity.hurt(new DamageSource(this.damageType), entity.level().getDifficulty() == Difficulty.EASY ? this.damageEasy : this.damage);
        }
        this.remainTicks--;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return null;
    }

    public int getInterval() {
        return this.interval;
    }

    public Optional<Integer> getOnSetDelay() {
        return this.onSetDelay;
    }

    public float getDamage() {
        return this.damage;
    }

    public Optional<Float> getDamageEasy() {
        return Optional.of(this.damageEasy);
    }

    public Holder<DamageType> getDamageType() {
        return this.damageType;
    }
}
