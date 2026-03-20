package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public record MultiplePower(Map<String, Power> subPowers) implements Power {
    private static final java.util.Set<String> RESERVED_KEYS = java.util.Set.of("type", "hidden", "condition", "name");

    public static final MapCodec<MultiplePower> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<MultiplePower> decode(DynamicOps<T> ops, MapLike<T> input) {
            Map<String, Power> subPowers = new LinkedHashMap<>();
            input.entries().forEach(pair -> {
                String key = ops.getStringValue(pair.getFirst()).result().orElse("");
                if (!RESERVED_KEYS.contains(key)) {
                    Power.DIRECT_CODEC.parse(ops, pair.getSecond()).resultOrPartial(e ->
                            Origins.LOGGER.warn("Failed to decode sub-power '{}' in multiple: {}", key, e)
                    ).ifPresent(power -> subPowers.put(key, power));
                }
            });
            return DataResult.success(new MultiplePower(subPowers));
        }

        @Override
        public <T> RecordBuilder<T> encode(MultiplePower input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            for (Map.Entry<String, Power> entry : input.subPowers().entrySet()) {
                Power.DIRECT_CODEC.encodeStart(ops, entry.getValue()).result()
                        .ifPresent(v -> prefix.add(entry.getKey(), v));
            }
            return prefix;
        }
    };

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull Entity entity) {
        this.subPowers.values().forEach(p -> p.grant(entity));
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        this.subPowers.values().forEach(p -> p.revoke(entity));
    }

    @Override
    public void tick(@NotNull Entity entity) {
        this.subPowers.values().forEach(p -> p.tick(entity));
    }

    @Override
    public void entityLoad(@NotNull Entity entity) {
        this.subPowers.values().forEach(p -> p.entityLoad(entity));
    }

    @Override
    public void entitySave(@NotNull Entity entity) {
        this.subPowers.values().forEach(p -> p.entitySave(entity));
    }
}
