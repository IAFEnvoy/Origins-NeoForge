package com.iafenvoy.origins.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public final class CombinedCodecs {
    public static final Codec<Either<Holder<MobEffect>, List<Holder<MobEffect>>>> MOB_EFFECT = Codec.either(MobEffect.CODEC, MobEffect.CODEC.listOf());
    public static final Codec<Either<MobEffectInstance, List<MobEffectInstance>>> MOB_EFFECT_INSTANCE = Codec.either(MobEffectInstance.CODEC, MobEffectInstance.CODEC.listOf());
    public static final Codec<Either<Holder<Enchantment>, List<Holder<Enchantment>>>> ENCHANTMENT = Codec.either(Enchantment.CODEC, Enchantment.CODEC.listOf());
}
