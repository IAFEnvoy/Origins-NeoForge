package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCameraSubmersionPower;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.data.power.builtin.regular.ShaderPower;
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
import net.minecraft.world.level.BlockGetter;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    public abstract void loadEffect(ResourceLocation resourceLocation);

    @Shadow
    @Nullable
    private PostChain postEffect;
    @Shadow
    private boolean effectActive;
    @Shadow
    @Final
    private ResourceManager resourceManager;
    @Unique
    private final HashMap<BlockPos, BlockState> origins$savedStates = new HashMap<>();
    @Unique
    private ResourceLocation currentlyLoadedShader;

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void nightVisionPatch(LivingEntity livingEntity, float nanoTime, CallbackInfoReturnable<Float> cir) {
        if (!livingEntity.hasEffect(MobEffects.NIGHT_VISION)) cir.setReturnValue(0F);
    }

    @Redirect(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;"))
    private FogType modifySubmersionType(Camera camera) {
        FogType original = camera.getFluidInCamera();
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
            Set<BlockPos> noLongerEyePositions = new HashSet<>();
            for (BlockPos p : this.origins$savedStates.keySet()) {
                if (!eyePositions.contains(p)) {
                    noLongerEyePositions.add(p);
                }
            }
            for (BlockPos eyePosition : noLongerEyePositions) {
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

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
    private void preventThirdPerson(Camera camera, BlockGetter getter, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        if (PhasingPower.hasRenderMethod(entity, PhasingPower.PhasingRenderType.REMOVE_BLOCKS))
            camera.setup(getter, entity, false, false, tickDelta);
        else
            camera.setup(getter, entity, thirdPerson, inverseView, tickDelta);
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
                this.currentlyLoadedShader = shaderLoc;
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
            if (this.currentlyLoadedShader != shader) {
                this.loadEffect(shader);
                this.currentlyLoadedShader = shader;
            }
        });
        if (shaderPowers.isEmpty() && this.currentlyLoadedShader != null) {
            if (this.postEffect != null) {
                this.postEffect.close();
                this.postEffect = null;
            }
            this.effectActive = false;
            this.currentlyLoadedShader = null;
        }
    }

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    private void disableShaderToggle(CallbackInfo ci) {
        if (OriginDataHolder.get(this.minecraft.getCameraEntity()).streamActivePowers(ShaderPower.class).anyMatch(power -> !power.isToggleable() && power.getShader().equals(this.currentlyLoadedShader)))
            ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "getNightVisionScale", cancellable = true)
    private static void updateNightVisionScale(LivingEntity living, float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (!living.hasEffect(MobEffects.NIGHT_VISION))
            OriginDataHolder.get(living).streamActivePowers(NightVisionPower.class).map(NightVisionPower::getStrength).max(Float::compareTo).ifPresent(cir::setReturnValue);
    }
}
