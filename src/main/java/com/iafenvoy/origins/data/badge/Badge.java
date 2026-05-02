package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.data.badge.builtin.EmptyBadge;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Badge {
    Codec<Badge> DIRECT_CODEC = DefaultedCodec.registryDispatch(BadgeRegistries.BADGE_TYPE, Badge::codec, Function.identity(), () -> EmptyBadge.INSTANCE);
    Codec<Holder<Badge>> CODEC = RegistryFixedCodec.create(BadgeRegistries.BADGE_KEY);

    @NotNull
    MapCodec<? extends Badge> codec();

    ResourceLocation spriteId();
}
