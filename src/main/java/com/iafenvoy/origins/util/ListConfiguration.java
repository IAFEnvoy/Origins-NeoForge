package com.iafenvoy.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Codec utility that accepts both singular and plural JSON field forms.
 * <p>Example: accepts {@code "modifier": {...}} and/or {@code "modifiers": [...]}
 * and combines them into a single {@code List}.
 */
public final class ListConfiguration {
    private ListConfiguration() {}

    /**
     * Creates a MapCodec that reads from both a singular and plural JSON field,
     * combining results into a single list.
     */
    public static <T> MapCodec<List<T>> mapCodec(Codec<T> elementCodec, String singular, String plural) {
        return new MapCodec<>() {
            @Override
            public <R> Stream<R> keys(DynamicOps<R> ops) {
                return Stream.of(ops.createString(singular), ops.createString(plural));
            }

            @Override
            public <R> DataResult<List<T>> decode(DynamicOps<R> ops, MapLike<R> input) {
                List<T> result = new ArrayList<>();
                R singleVal = input.get(singular);
                if (singleVal != null) {
                    elementCodec.parse(ops, singleVal).result().ifPresent(result::add);
                }
                R listVal = input.get(plural);
                if (listVal != null) {
                    elementCodec.listOf().parse(ops, listVal).result().ifPresent(result::addAll);
                }
                return DataResult.success(List.copyOf(result));
            }

            @Override
            public <R> RecordBuilder<R> encode(List<T> input, DynamicOps<R> ops, RecordBuilder<R> prefix) {
                if (input.size() == 1) {
                    prefix.add(singular, elementCodec.encodeStart(ops, input.getFirst()));
                } else if (!input.isEmpty()) {
                    prefix.add(plural, elementCodec.listOf().encodeStart(ops, input));
                }
                return prefix;
            }
        };
    }

    /** Standard modifier codec: reads "modifier" (single) and/or "modifiers" (list). */
    public static final MapCodec<List<Modifier>> MODIFIER_CODEC =
            mapCodec(Modifier.CODEC, "modifier", "modifiers");

    /** Named modifier codec: reads "{name}" (single) and/or "{name}s" (list). */
    public static MapCodec<List<Modifier>> modifierCodec(String name) {
        return mapCodec(Modifier.CODEC, name, name + "s");
    }
}
