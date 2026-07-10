package com.iafenvoy.origins.util.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.K;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class CollectionCodecs {
    public static <K, V> Codec<Map<K, V>> ofAutoIgnore(Codec<K> key, Codec<V> value) {
        return new AutoIgnoreMapCodec<>(key, value);
    }

    public static <V> Codec<List<V>> ofAutoIgnore(Codec<V> value) {
        return new AutoIgnoreListCodec<>(value);
    }

    public static <K, V> Codec<Multimap<K, V>> multiMapCodec(Codec<K> key, Codec<V> value) {
        return ofAutoIgnore(key, value.listOf()).xmap(m -> {
            Multimap<K, V> multiMap = HashMultimap.create();
            m.forEach(multiMap::putAll);
            return multiMap;
        }, m -> m.keySet().stream().collect(Collectors.toMap(Function.identity(), k -> List.copyOf(m.get(k)), (a, b) -> b, LinkedHashMap::new)));
    }

    @SuppressWarnings("unchecked")
    public static <T> Codec<Map<Class<? extends T>, T>> classMapCodec(Codec<T> codec) {
        return ofAutoIgnore(codec).xmap(l -> l.stream().collect(Collectors.toMap(t -> (Class<? extends T>) t.getClass(), Function.identity(), (a, b) -> b, LinkedHashMap::new)), m -> List.copyOf(m.values()));
    }

    public static <C extends Container> Codec<C> containerCodec(Supplier<C> factory) {
        return ofAutoIgnore(ItemStack.OPTIONAL_CODEC).xmap(l -> {
            C container = factory.get();
            for (int i = 0; i < Math.min(container.getContainerSize(), l.size()); i++) container.setItem(i, l.get(i));
            return container;
        }, c -> {
            List<ItemStack> stacks = new LinkedList<>();
            for (int i = 0; i < c.getContainerSize(); i++) stacks.add(c.getItem(i));
            return stacks;
        });
    }

    public static <K> Codec<Object2IntMap<K>> toIntMap(Codec<K> keyCodec) {
        return Codec.unboundedMap(keyCodec, Codec.INT).xmap(Object2IntOpenHashMap::new, Function.identity());
    }
}
