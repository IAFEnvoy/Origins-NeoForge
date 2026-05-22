package com.iafenvoy.origins.util.codec;

import com.iafenvoy.origins.accessor.ResourceLoadingOps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public enum WildcardCodec implements Codec<ResourceLocation> {
    INSTANCE;

    @Override
    public <T> DataResult<Pair<ResourceLocation, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getStringValue(input).flatMap(s -> {
            if (s.contains("*")) {
                if (ops instanceof ResourceLoadingOps keyGetter) {
                    ResourceKey<?> resourceKey = keyGetter.origins$getKey();
                    if (resourceKey != null) {
                        ResourceLocation key = resourceKey.location();
                        String[] split = s.split(":");
                        if (split.length == 2) {
                            ResourceLocation id = ResourceLocation.tryBuild(split[0].replaceAll("\\*", key.getNamespace()), split[1].replaceAll("\\*", key.getPath()));
                            if (id != null) return DataResult.success(Pair.of(id, ops.empty()));
                        }
                        return DataResult.error(() -> "Invalid wildcard resource location: " + s + ", expected format: <namespace>:<path>");
                    }
                }
                return DataResult.error(() -> "Wildcard resource location cannot be decoded without a key context, which is only available in resource loading.");
            } else return ResourceLocation.CODEC.decode(ops, input);
        });
    }

    @Override
    public <T> DataResult<T> encode(ResourceLocation input, DynamicOps<T> ops, T prefix) {
        return ResourceLocation.CODEC.encode(input, ops, prefix);
    }
}
