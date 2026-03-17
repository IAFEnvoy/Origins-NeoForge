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

public record ParticlePower(ParticleType<?> particle, int frequency,
                            EntityCondition condition) implements Power {
    public static final MapCodec<ParticlePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle").forGetter(ParticlePower::particle),
            Codec.INT.optionalFieldOf("frequency", 4).forGetter(ParticlePower::frequency),
            EntityCondition.optionalCodec("condition").forGetter(ParticlePower::condition)
    ).apply(i, ParticlePower::new));

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
