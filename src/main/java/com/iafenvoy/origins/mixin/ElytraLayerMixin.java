package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ElytraFlightPower;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin<T extends LivingEntity> {
    @Unique
    private LivingEntity origins$livingEntity;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void modifyEquippedStackToElytra(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        this.origins$livingEntity = entity;
        if (OriginDataHolder.get(entity).streamActivePowers(ElytraFlightPower.class).anyMatch(ElytraFlightPower::shouldRenderElytra) && !entity.isInvisible())
            cir.setReturnValue(true);
    }

    @Inject(method = "getElytraTexture", at = @At("HEAD"), cancellable = true)
    private void setTexture(ItemStack stack, T entity, CallbackInfoReturnable<ResourceLocation> cir) {
        OriginDataHolder.get(this.origins$livingEntity).streamActivePowers(ElytraFlightPower.class).map(ElytraFlightPower::getTextureLocation).flatMap(Optional::stream).findFirst().ifPresent(cir::setReturnValue);
    }
}
