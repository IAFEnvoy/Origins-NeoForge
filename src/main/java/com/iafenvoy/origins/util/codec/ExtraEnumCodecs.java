package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;

import java.util.Locale;

public final class ExtraEnumCodecs {
    public static final Codec<Dist> DIST = Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> Dist.valueOf(x.toUpperCase(Locale.ROOT)));
    public static final Codec<SoundSource> SOUND_SOURCE = ExtraCodecs.idResolverCodec(SoundSource::ordinal, x -> SoundSource.values()[x], 0);
}
