package com.iafenvoy.origins.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public final class CombinedCodecs {
    public static final Codec<Either<MobEffectInstance, List<MobEffectInstance>>> MOB_EFFECT_INSTANCE = Codec.either(MobEffectInstance.CODEC, MobEffectInstance.CODEC.listOf());
}
