package com.iafenvoy.origins.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {
    @Accessor("hiddenEffect")
    @Nullable
    MobEffectInstance getHiddenEffect();
}
