package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;

import java.util.Optional;
import java.util.List;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MultiplePower extends Power {
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
            // Try to parse BaseSettings from the same map if present; otherwise use defaults
            BaseSettings settings = BaseSettings.CODEC.decode(ops, input).result().orElse(new BaseSettings(Optional.empty(), Optional.empty(), false, AlwaysTrueCondition.INSTANCE, 0, List.of()));
            return DataResult.success(new MultiplePower(settings, subPowers));
        }

        @Override
        public <T> RecordBuilder<T> encode(MultiplePower input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            for (Map.Entry<String, Power> entry : input.getSubPowers().entrySet()) {
                Power.DIRECT_CODEC.encodeStart(ops, entry.getValue()).result()
                        .ifPresent(v -> prefix.add(entry.getKey(), v));
            }
            return prefix;
        }
    };
    private final Map<String, Power> subPowers;

    private static final java.util.Set<String> RESERVED_KEYS = java.util.Set.of("type", "hidden", "condition", "name");

    public MultiplePower(BaseSettings settings, Map<String, Power> subPowers) {
        super(settings);
        this.subPowers = subPowers;
    }

    public Map<String, Power> getSubPowers() {
        return this.subPowers;
    }

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
