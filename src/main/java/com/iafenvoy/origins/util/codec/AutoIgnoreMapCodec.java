package com.iafenvoy.origins.util.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.BaseMapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Function;

public record AutoIgnoreMapCodec<K, V>(Codec<K> keyCodec,
                                       Codec<V> elementCodec) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        Object2ObjectMap<K, V> read = new Object2ObjectArrayMap<>();
        DataResult<Unit> result = input.entries().reduce(DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (r, pair) -> {
                    DataResult<K> key = this.keyCodec().parse(ops, pair.getFirst());
                    DataResult<V> value = this.elementCodec().parse(ops, pair.getSecond());
                    //Key modify point, ignore all errors
                    if (key.isError()) {
                        LOGGER.warn("Failed to decode key: {}, error: {}", pair.getFirst(), key.error().orElseThrow());
                        return r;
                    }
                    if (value.isError()) {
                        LOGGER.warn("Failed to decode value: {}, error: {}", pair.getSecond(), value.error().orElseThrow());
                        return r;
                    }
                    return r.apply2stable((u, p) -> u, key.apply2stable(Pair::of, value));
                }, (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );
        Map<K, V> elements = ImmutableMap.copyOf(read);
        return result.map(unit -> elements).setPartial(elements);
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).map(map -> this.decode(ops, map)).flatMap(Function.identity()).map(map -> new Pair<>(map, input));
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return this.encode(input, ops, ops.mapBuilder()).build(prefix);
    }
}
