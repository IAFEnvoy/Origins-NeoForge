package com.iafenvoy.origins.util.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionCodecs {
    public static <K, V> Codec<Multimap<K, V>> multiMapCodec(Codec<K> key, Codec<V> value) {
        return Codec.unboundedMap(key, value.listOf()).xmap(m -> {
            Multimap<K, V> multiMap = HashMultimap.create();
            m.forEach(multiMap::putAll);
            return multiMap;
        }, mm -> mm.keySet().stream().collect(Collectors.toMap(Function.identity(), k -> List.copyOf(mm.get(k)), (a, b) -> b, LinkedHashMap::new)));
    }

    @SuppressWarnings("unchecked")
    public static <T> Codec<Map<Class<? extends T>, T>> classMapCodec(Codec<T> codec) {
        return codec.listOf().xmap(l -> l.stream().collect(Collectors.toMap(t -> (Class<? extends T>) t.getClass(), Function.identity(), (a, b) -> b, LinkedHashMap::new)), m -> List.copyOf(m.values()));
    }
}
