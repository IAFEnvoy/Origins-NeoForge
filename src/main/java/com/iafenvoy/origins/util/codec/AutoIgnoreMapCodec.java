package com.iafenvoy.origins.util.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.BaseMapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record AutoIgnoreMapCodec<K, V>(Codec<K> keyCodec,
                                       Codec<V> elementCodec) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {
    @Override
    public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
        final Object2ObjectMap<K, V> read = new Object2ObjectArrayMap<>();
        final ImmutableMap.Builder<T, T> failed = ImmutableMap.builder();

        final DataResult<Unit> result = input.entries().reduce(
                DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
                (r, pair) -> {
                    final DataResult<K> key = this.keyCodec().parse(ops, pair.getFirst());
                    final DataResult<V> value = this.elementCodec().parse(ops, pair.getSecond());
                    //Key modify point, ignore all errors
                    if (key.isError() || value.isError()) return r;
                    final DataResult<Pair<K, V>> entryResult = key.apply2stable(Pair::of, value);
                    final Optional<Pair<K, V>> entry = entryResult.resultOrPartial();
                    if (entry.isPresent()) {
                        final V existingValue = read.putIfAbsent(entry.get().getFirst(), entry.get().getSecond());
                        if (existingValue != null) {
                            failed.put(pair.getFirst(), pair.getSecond());
                            return r.apply2stable((u, p) -> u, DataResult.error(() -> "Duplicate entry for key: '" + entry.get().getFirst() + "'"));
                        }
                    }
                    if (entryResult.isError()) failed.put(pair.getFirst(), pair.getSecond());
                    return r.apply2stable((u, p) -> u, entryResult);
                },
                (r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
        );

        final Map<K, V> elements = ImmutableMap.copyOf(read);
        final T errors = ops.createMap(failed.build());

        return result.map(unit -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
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
