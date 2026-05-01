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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {
    @Unique
    private LivingEntity origins$livingEntity;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifyEquippedStackToElytra(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        this.origins$livingEntity = entity;
        if (OriginDataHolder.get(entity).streamActivePowers(ElytraFlightPower.class).anyMatch(ElytraFlightPower::shouldRenderElytra) && !entity.isInvisible())
            cir.setReturnValue(true);
    }

    @ModifyArg(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private ResourceLocation setTexture(ResourceLocation texture) {
        return OriginDataHolder.get(this.origins$livingEntity).streamActivePowers(ElytraFlightPower.class).map(ElytraFlightPower::getTextureLocation).flatMap(Optional::stream).findFirst().orElse(texture);
    }

	/*
	//This would be the proper way to do this, but it wouldn't override capes if applicable.
	@Inject(method = "getElytraTexture", at = @At("TAIL"), cancellable = true)
	private void overrideTextureProperly(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
		ElytraFlightPower.getElytraTexture(entity).ifPresent(cir::setReturnValue);
	}
	*/
}
