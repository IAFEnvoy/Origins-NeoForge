package com.iafenvoy.origins.mixin.codec;

import com.iafenvoy.origins.accessor.ResourceLoadingOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RegistryOps.class)
public class RegistryOpsMixin implements ResourceLoadingOps {
    @Unique
    private final ThreadLocal<ResourceKey<?>> origins$key = new ThreadLocal<>();

    @Override
    public void origins$setKey(@Nullable ResourceKey<?> key) {
        if (key == null) this.origins$key.remove();
        else this.origins$key.set(key);
    }

    @Nullable
    @Override
    public ResourceKey<?> origins$getKey() {
        return this.origins$key.get();
    }
}
