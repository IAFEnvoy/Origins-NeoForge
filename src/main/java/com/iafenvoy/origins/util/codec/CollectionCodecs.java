package com.iafenvoy.origins.util.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
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

    public static <C extends Container> Codec<C> containerCodec(Supplier<C> factory) {
        return ItemStack.OPTIONAL_CODEC.listOf().xmap(l -> {
            C container = factory.get();
            for (int i = 0; i < Math.min(container.getContainerSize(), l.size()); i++) container.setItem(i, l.get(i));
            return container;
        }, c -> {
            List<ItemStack> stacks = new LinkedList<>();
            for (int i = 0; i < c.getContainerSize(); i++) stacks.add(c.getItem(i));
            return stacks;
        });
    }
}
