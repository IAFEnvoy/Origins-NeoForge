package com.iafenvoy.origins.accessor;

import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface ResourceLoadingOps {
    void origins$setKey(@Nullable ResourceKey<?> key);

    @Nullable
    ResourceKey<?> origins$getKey();
}
