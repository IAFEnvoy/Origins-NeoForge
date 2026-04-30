package com.iafenvoy.origins.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.function.Function;

public final class ComponentCodec {
    public static final Codec<Component> TRANSLATE_FIRST = Codec.either(Codec.STRING, ComponentSerialization.CODEC).xmap(x -> x.map(Component::translatable, Function.identity()), Either::right);
}
