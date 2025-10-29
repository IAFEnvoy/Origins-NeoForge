package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.neoforged.api.distmarker.Dist;

import java.util.Locale;
import java.util.function.Function;

public final class ExtraEnumCodecs {
    public static final Codec<Dist> DIST = enumCodec(Dist::valueOf);
    public static final Codec<SoundSource> SOUND_SOURCE = enumCodec(SoundSource::valueOf);
    public static final Codec<ClipContext.Block> CLIP_CONTEXT_BLOCK = enumCodec(ClipContext.Block::valueOf);
    public static final Codec<ClipContext.Fluid> CLIP_CONTEXT_FLUID = enumCodec(ClipContext.Fluid::valueOf);

    public static <T extends Enum<T>> Codec<T> enumCodec(Function<String, T> stringSolver) {
        return Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> stringSolver.apply(x.toUpperCase(Locale.ROOT)));
    }
}
