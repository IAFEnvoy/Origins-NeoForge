package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ParticlePower extends Power {
    public static final MapCodec<ParticlePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle").forGetter(ParticlePower::getParticle),
            Codec.INT.optionalFieldOf("frequency", 4).forGetter(ParticlePower::getFrequency),
            EntityCondition.optionalCodec("condition").forGetter(ParticlePower::getCondition)
    ).apply(i, ParticlePower::new));
    private final ParticleType<?> particle;
    private final int frequency;
    private final EntityCondition condition;

    public ParticlePower(BaseSettings settings, ParticleType<?> particle, int frequency, EntityCondition condition) {
        super(settings);
        this.particle = particle;
        this.frequency = frequency;
        this.condition = condition;
    }

    public ParticleType<?> getParticle() {
        return this.particle;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel && entity.tickCount % this.frequency == 0) {
            if (this.condition.test(entity) && this.particle instanceof ParticleOptions options) {
                serverLevel.sendParticles(options, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                        1, entity.getBbWidth() * 0.3, entity.getBbHeight() * 0.3, entity.getBbWidth() * 0.3, 0.01);
            }
        }
    }
}
