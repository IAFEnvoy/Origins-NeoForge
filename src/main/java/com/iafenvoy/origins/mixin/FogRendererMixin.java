package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCameraSubmersionPower;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@OnlyIn(Dist.CLIENT)
@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 0), ordinal = 0)
    private static FogType modifyCameraSubmersionTypeRender(FogType original, Camera camera) {
        return ModifyCameraSubmersionPower.tryReplace(camera.getEntity(), original).orElse(original);
    }

    @ModifyVariable(method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 0), ordinal = 0)
    private static FogType modifyCameraSubmersionTypeFog(FogType original, Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float partialTicks) {
        return ModifyCameraSubmersionPower.tryReplace(camera.getEntity(), original).orElse(original);
    }

    //I don't know exactly what this does, but it seems harmless.
    @ModifyVariable(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0, shift = At.Shift.AFTER), ordinal = 2)
    private static float modifyFogDensityForPhasingBlindness(float original, Camera camera) {
        if (camera.getEntity() instanceof LivingEntity living && PhasingPower.hasRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS) && PhasingPower.getInWallBlockState(living) != null)
            return 0;
        return original;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0), method = "setupColor")
    private static boolean hasStatusEffectProxy(LivingEntity instance, Holder<MobEffect> effect) {
        if (instance instanceof Player && effect == MobEffects.NIGHT_VISION && !instance.hasEffect(MobEffects.NIGHT_VISION))
            return OriginDataHolder.get(instance).streamActivePowers(NightVisionPower.class).map(NightVisionPower::getStrength).max(Float::compareTo).isPresent();
        return instance.hasEffect(effect);
    }
}
