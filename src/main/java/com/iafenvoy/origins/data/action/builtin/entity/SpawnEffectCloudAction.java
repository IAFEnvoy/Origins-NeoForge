package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.CombinedCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SpawnEffectCloudAction(float radius, float radiusOnUse, int waitTime,
                                     Either<MobEffectInstance, List<MobEffectInstance>> effect) implements EntityAction {
    public static final MapCodec<SpawnEffectCloudAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("radius", 3F).forGetter(SpawnEffectCloudAction::radius),
            Codec.FLOAT.optionalFieldOf("radius_on_use", -0.5F).forGetter(SpawnEffectCloudAction::radiusOnUse),
            Codec.INT.optionalFieldOf("wait_time", 10).forGetter(SpawnEffectCloudAction::waitTime),
            CombinedCodecs.MOB_EFFECT_INSTANCE.optionalFieldOf("effect", Either.right(List.of())).forGetter(SpawnEffectCloudAction::effect)
    ).apply(i, SpawnEffectCloudAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source.level() instanceof ServerLevel serverLevel)
            EntityType.AREA_EFFECT_CLOUD.spawn(serverLevel, c -> {
                c.setRadius(this.radius);
                c.setRadiusOnUse(this.radiusOnUse);
                c.setWaitTime(this.waitTime);
                this.effect.map(List::of, x -> x).stream().map(MobEffectInstance::new).forEach(c::addEffect);
            }, source.blockPosition(), MobSpawnType.MOB_SUMMONED, false, false);
    }
}
