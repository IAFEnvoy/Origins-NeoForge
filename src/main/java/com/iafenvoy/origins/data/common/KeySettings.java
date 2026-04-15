package com.iafenvoy.origins.data.common;

import com.iafenvoy.origins.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record KeySettings(String key, boolean continuous) {
    public static final MapCodec<KeySettings> CODEC = RecordCodecBuilder.<KeySettings>create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("key", Constants.PRIMARY_ACTIVE_KEY).forGetter(KeySettings::key),
            Codec.BOOL.optionalFieldOf("continuous", false).forGetter(KeySettings::continuous)
    ).apply(instance, KeySettings::new)).optionalFieldOf("key", new KeySettings(Constants.PRIMARY_ACTIVE_KEY, false));
}
