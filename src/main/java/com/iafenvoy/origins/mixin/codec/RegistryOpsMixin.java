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
    @Nullable
    private ResourceKey<?> origins$key;

    @Override
    public void origins$setKey(@Nullable ResourceKey<?> key) {
        this.origins$key = key;
    }

    @Nullable
    @Override
    public ResourceKey<?> origins$getKey() {
        return this.origins$key;
    }
}
