package com.iafenvoy.origins.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

//These codec can recognize both singleton and array
public final class CombinedCodecs {
    public static final Codec<List<Holder<MobEffect>>> MOB_EFFECT = combineCodec(MobEffect.CODEC);
    public static final Codec<List<MobEffectInstance>> MOB_EFFECT_INSTANCE = combineCodec(MobEffectInstance.CODEC);
    public static final Codec<List<Holder<Enchantment>>> ENCHANTMENT = combineCodec(Enchantment.CODEC);
    public static final Codec<List<Component>> TEXT = combineCodec(ComponentSerialization.CODEC);

    public static <T> Codec<List<T>> combineCodec(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(x -> x.map(List::of, l -> l), l -> l.size() == 1 ? Either.left(l.getFirst()) : Either.right(l));
    }
}
