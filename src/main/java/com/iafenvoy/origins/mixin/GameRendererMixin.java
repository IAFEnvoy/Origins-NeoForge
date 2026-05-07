package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCameraSubmersionPower;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.data.power.builtin.regular.ShaderPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    @Final
    Minecraft minecraft;
    @Shadow
    @Nullable
    PostChain postEffect;
    @Shadow
    private boolean effectActive;
    @Shadow
    @Final
    private ResourceManager resourceManager;
    @Unique
    private final HashMap<BlockPos, BlockState> origins$savedStates = new HashMap<>();
    @Unique
    private ResourceLocation origins$currentlyLoadedShader;

    @Shadow
    public abstract void loadEffect(ResourceLocation resourceLocation);

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void nightVisionPatch(LivingEntity livingEntity, float nanoTime, CallbackInfoReturnable<Float> cir) {
        if (!livingEntity.hasEffect(MobEffects.NIGHT_VISION)) cir.setReturnValue(0F);
    }

    @ModifyExpressionValue(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;"))
    private FogType modifySubmersionType(FogType original, Camera camera) {
        return ModifyCameraSubmersionPower.tryReplace(camera.getEntity(), original).orElse(original);
    }

    // PHASING: remove_blocks
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void beforeRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        //noinspection ConstantValue
        if (this.mainCamera.getEntity() == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;
        if (PhasingPower.getRenderMethod(this.mainCamera.getEntity(), PhasingPower.PhasingRenderType.REMOVE_BLOCKS).isPresent()) {
            Set<BlockPos> eyePositions = this.origins$getEyePos(0.25F, 0.05F, 0.25F);
            for (BlockPos eyePosition : this.origins$savedStates.keySet().stream().filter(p -> !eyePositions.contains(p)).collect(Collectors.toSet())) {
                BlockState state = this.origins$savedStates.get(eyePosition);
                level.setBlockAndUpdate(eyePosition, state);
                this.origins$savedStates.remove(eyePosition);
            }
            for (BlockPos p : eyePositions) {
                BlockState stateAtP = level.getBlockState(p);
                if (!this.origins$savedStates.containsKey(p) && !level.isEmptyBlock(p) && !(stateAtP.getBlock() instanceof LiquidBlock)) {
                    this.origins$savedStates.put(p, stateAtP);
                    level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                }
            }
        } else if (!this.origins$savedStates.isEmpty()) {
            Set<BlockPos> noLongerEyePositions = new HashSet<>(this.origins$savedStates.keySet());
            for (BlockPos eyePosition : noLongerEyePositions) {
                BlockState state = this.origins$savedStates.get(eyePosition);
                level.setBlockAndUpdate(eyePosition, state);
                this.origins$savedStates.remove(eyePosition);
            }
        }
    }

    @ModifyArgs(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
    private void modifyCameraSetupArgs(Args args, @Local Entity entity) {
        if (PhasingPower.hasRenderMethod(entity, PhasingPower.PhasingRenderType.REMOVE_BLOCKS)) {
            args.set(2, false);
            args.set(3, false);
        }
    }

    @Unique
    private Set<BlockPos> origins$getEyePos(float rangeX, float rangeY, float rangeZ) {
        Vec3 pos = this.mainCamera.getEntity().position().add(0, this.mainCamera.getEntity().getEyeHeight(this.mainCamera.getEntity().getPose()), 0);
        AABB cameraBox = new AABB(pos, pos);
        cameraBox = cameraBox.inflate(rangeX, rangeY, rangeZ);
        HashSet<BlockPos> set = new HashSet<>();
        BlockPos.betweenClosedStream(cameraBox).forEach(p -> set.add(p.immutable()));
        return set;
    }

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    private void loadShaderFromPowerOnCameraEntity(Entity entity, CallbackInfo ci) {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity == null) return;
        OriginDataHolder.get(cameraEntity).streamActivePowers(ShaderPower.class).forEach(x -> {
            ResourceLocation shaderLoc = x.getShader();
            if (this.resourceManager.getResource(shaderLoc).isPresent()) {
                this.loadEffect(shaderLoc);
                this.origins$currentlyLoadedShader = shaderLoc;
            }
        });
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void loadShaderFromPower(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity == null) return;
        List<ShaderPower> shaderPowers = OriginDataHolder.get(cameraEntity).streamActivePowers(ShaderPower.class).toList();
        shaderPowers.forEach(x -> {
            ResourceLocation shader = x.getShader();
            if (this.origins$currentlyLoadedShader != shader) {
                this.loadEffect(shader);
                this.origins$currentlyLoadedShader = shader;
            }
        });
        if (shaderPowers.isEmpty() && this.origins$currentlyLoadedShader != null) {
            if (this.postEffect != null) {
                this.postEffect.close();
                this.postEffect = null;
            }
            this.effectActive = false;
            this.origins$currentlyLoadedShader = null;
        }
    }

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    private void disableShaderToggle(CallbackInfo ci) {
        if (OriginDataHolder.get(this.minecraft.getCameraEntity()).streamActivePowers(ShaderPower.class).anyMatch(power -> !power.isToggleable() && power.getShader().equals(this.origins$currentlyLoadedShader)))
            ci.cancel();
    }

    @ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float updateNightVisionScale(float original, LivingEntity living, float tickDelta) {
        return !living.hasEffect(MobEffects.NIGHT_VISION) ? OriginDataHolder.get(living).streamActivePowers(NightVisionPower.class).map(NightVisionPower::getStrength).max(Float::compareTo).orElse(original) : original;
    }
}
