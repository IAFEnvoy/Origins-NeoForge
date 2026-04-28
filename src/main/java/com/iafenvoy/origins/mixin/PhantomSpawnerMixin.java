package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyInsomniaTicksPower;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
    // This is imo a better way to handle this, so then the modified insomnia ticks will be properly clamped.
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"), index = 0)
    private int modifyTicks(int original, @Local ServerPlayer serverplayer) {
        return OriginDataHolder.get(serverplayer).getHelper().modify(ModifyInsomniaTicksPower.class, original);
    }
}
