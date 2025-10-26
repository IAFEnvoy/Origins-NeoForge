package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record SpawnParticlesAction(ParticleType<?> particle, BiEntityCondition biEntityCondition, int count,
                                   float speed, boolean force, Vec3 spread, float offsetX, float offsetY,
                                   float offsetZ) implements EntityAction {
    public static final MapCodec<SpawnParticlesAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle").forGetter(SpawnParticlesAction::particle),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SpawnParticlesAction::biEntityCondition),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(SpawnParticlesAction::count),
            Codec.FLOAT.optionalFieldOf("speed", 0F).forGetter(SpawnParticlesAction::speed),
            Codec.BOOL.optionalFieldOf("force", false).forGetter(SpawnParticlesAction::force),
            Vec3.CODEC.optionalFieldOf("spread", new Vec3(0.5, 0.5, 0.5)).forGetter(SpawnParticlesAction::spread),
            Codec.FLOAT.optionalFieldOf("offset_x", 0F).forGetter(SpawnParticlesAction::offsetX),
            Codec.FLOAT.optionalFieldOf("offset_y", 0.5F).forGetter(SpawnParticlesAction::offsetY),
            Codec.FLOAT.optionalFieldOf("offset_z", 0F).forGetter(SpawnParticlesAction::offsetZ)
    ).apply(i, SpawnParticlesAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source.level() instanceof ServerLevel serverLevel) {
            Vec3 delta = this.spread.multiply(source.getBbWidth(), source.getEyeHeight(source.getPose()), source.getBbWidth());
            Vec3 pos = source.position().add(this.offsetX, this.offsetY, this.offsetZ);
            //FIXME::Not get options in this way
            if (this.particle instanceof ParticleOptions options)
                for (ServerPlayer player : serverLevel.players()) {
                    if (this.biEntityCondition.test(source, player))
                        serverLevel.sendParticles(player, options, this.force, pos.x, pos.y, pos.z, this.count, delta.x, delta.y, delta.z, this.speed);
                }
        }
    }
}
