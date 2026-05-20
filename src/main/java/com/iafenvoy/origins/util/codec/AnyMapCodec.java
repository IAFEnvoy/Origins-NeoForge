package com.iafenvoy.origins.util.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AnyMapCodec<A> extends MapCodec<Map<String, A>> {
    private final List<String> knownKey;
    private final Codec<A> valueCodec;

    protected AnyMapCodec(List<String> knownKey, Codec<A> valueCodec) {
        this.knownKey = knownKey;
        this.valueCodec = valueCodec;
    }

    public static <A> AnyMapCodec<A> create(List<String> knownKey, Codec<A> valueCodec) {
        return new AnyMapCodec<>(knownKey, valueCodec);
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(ops.createString("<subpower>"));
    }

    @Override
    public <T> DataResult<Map<String, A>> decode(DynamicOps<T> ops, MapLike<T> input) {
        ImmutableMap.Builder<String, A> builder = ImmutableMap.builder();
        input.entries().map(x -> x.mapFirst(ops::getStringValue))
                .filter(x -> x.getFirst().isSuccess())
                .map(x -> x.mapFirst(DataResult::getOrThrow))
                .filter(x -> !this.knownKey.contains(x.getFirst()))
                .map(x -> x.mapSecond(t -> this.valueCodec.decode(ops, t)))
                .map(x -> x.mapSecond(DataResult::getOrThrow))
                .map(x -> x.mapSecond(Pair::getFirst))
                .forEach(x -> builder.put(x.getFirst(), x.getSecond()));
        return DataResult.success(builder.buildKeepingLast());
    }

    @Override
    public <T> RecordBuilder<T> encode(Map<String, A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        input.entrySet().stream().filter(x -> !this.knownKey.contains(x.getKey()))
                .forEach(x -> prefix.add(x.getKey(), this.valueCodec.encodeStart(ops, x.getValue())));
        return prefix;
    }
}
