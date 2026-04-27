package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.Optional;

public record KeySettings(String key, boolean continuous) {
    public static final Codec<KeySettings> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("key", Constants.PRIMARY_ACTIVE_KEY).forGetter(KeySettings::key),
            Codec.BOOL.optionalFieldOf("continuous", false).forGetter(KeySettings::continuous)
    ).apply(instance, KeySettings::new));
    public static final MapCodec<KeySettings> CODEC = BASE_CODEC.optionalFieldOf("key", new KeySettings(Constants.PRIMARY_ACTIVE_KEY, false));
    public static final MapCodec<Optional<KeySettings>> OPTIONAL_CODEC = BASE_CODEC.optionalFieldOf("key");

    public boolean match(String key) {
        return Objects.equals(this.key, key);
    }
}
