package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WeightedActionEntry<T>(T element, int weight) implements WeightedRandomSelector {
    public static <T> Codec<WeightedActionEntry<T>> codec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(i -> i.group(
                elementCodec.fieldOf("element").forGetter(WeightedActionEntry::element),
                Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedActionEntry::weight)
        ).apply(i, WeightedActionEntry::new));
    }
}
