package com.iafenvoy.origins.util.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionCodecs {
    public static <K, V> Codec<Multimap<K, V>> multiMapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(keyCodec, valueCodec.listOf()).xmap(m -> {
            Multimap<K, V> multiMap = HashMultimap.create();
            m.forEach(multiMap::putAll);
            return multiMap;
        }, mm -> mm.keySet().stream().collect(Collectors.toMap(Function.identity(), key -> List.copyOf(mm.get(key)), (a, b) -> b, LinkedHashMap::new)));
    }
}
