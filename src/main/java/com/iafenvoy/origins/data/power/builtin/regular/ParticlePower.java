package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

//FIXME::Full params https://origins.readthedocs.io/en/latest/types/power_types/particle/
public class ParticlePower extends Power {
    public static final MapCodec<ParticlePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticlePower::getParticle),
            Codec.INT.optionalFieldOf("frequency", 4).forGetter(ParticlePower::getFrequency)
    ).apply(i, ParticlePower::new));
    private final ParticleOptions particle;
    private final int frequency;

    public ParticlePower(BaseSettings settings, ParticleOptions particle, int frequency) {
        super(settings);
        this.particle = particle;
        this.frequency = frequency;
    }

    public ParticleOptions getParticle() {
        return this.particle;
    }

    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void tick(@NotNull OriginDataHolder holder) {
        super.tick(holder);
        if (!this.isActive(holder)) return;
        Entity entity = holder.getEntity();
        if (entity.level() instanceof ServerLevel serverLevel && entity.tickCount % this.frequency == 0)
            serverLevel.sendParticles(this.particle, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                    1, entity.getBbWidth() * 0.3, entity.getBbHeight() * 0.3, entity.getBbWidth() * 0.3, 0.01);
    }
}
