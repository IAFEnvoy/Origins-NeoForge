package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.data.power.builtin.regular.ShaderPower;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera mainCamera;
    @Shadow @Final Minecraft minecraft;
    @Shadow private @Nullable Identifier postEffectId;
    @Shadow private boolean effectActive;
    @Unique private final HashMap<BlockPos, BlockState> origins$savedStates = new HashMap<>();
    @Unique private Identifier origins$currentlyLoadedShader;

    @Shadow public abstract void setPostEffect(Identifier id);
    @Shadow public abstract void clearPostEffect();

    @Inject(method = "render", at = @At("HEAD"))
    private void origins$updatePowerRendering(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        this.origins$updateRemovedBlocks();
        this.origins$updateShaderPower();
    }

    @Unique
    private void origins$updateRemovedBlocks() {
        Entity cameraEntity = this.mainCamera.entity();
        ClientLevel level = this.minecraft.level;
        if (cameraEntity == null || level == null) return;

        if (PhasingPower.hasRenderMethod(cameraEntity, PhasingPower.PhasingRenderType.REMOVE_BLOCKS)) {
            Set<BlockPos> eyePositions = this.origins$getEyePos(cameraEntity, 0.25F, 0.05F, 0.25F);
            for (BlockPos pos : this.origins$savedStates.keySet().stream().filter(p -> !eyePositions.contains(p)).collect(Collectors.toSet())) {
                level.setBlockAndUpdate(pos, this.origins$savedStates.remove(pos));
            }
            for (BlockPos pos : eyePositions) {
                BlockState state = level.getBlockState(pos);
                if (!this.origins$savedStates.containsKey(pos) && !level.isEmptyBlock(pos) && !(state.getBlock() instanceof LiquidBlock)) {
                    this.origins$savedStates.put(pos, state);
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        } else {
            for (BlockPos pos : new HashSet<>(this.origins$savedStates.keySet())) {
                level.setBlockAndUpdate(pos, this.origins$savedStates.remove(pos));
            }
        }
    }

    @Unique
    private Set<BlockPos> origins$getEyePos(Entity entity, float rangeX, float rangeY, float rangeZ) {
        Vec3 pos = entity.position().add(0, entity.getEyeHeight(entity.getPose()), 0);
        Set<BlockPos> result = new HashSet<>();
        BlockPos.betweenClosedStream(new AABB(pos, pos).inflate(rangeX, rangeY, rangeZ)).forEach(p -> result.add(p.immutable()));
        return result;
    }

    @Unique
    private void origins$updateShaderPower() {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity == null) return;
        List<ShaderPower> powers = OriginDataHolder.get(cameraEntity).streamActivePowers(ShaderPower.class).toList();
        if (!powers.isEmpty()) {
            Identifier shader = origins$normalizePostEffectId(powers.getFirst().getShader());
            if (!Objects.equals(shader, this.origins$currentlyLoadedShader)) {
                this.setPostEffect(shader);
                this.origins$currentlyLoadedShader = shader;
            }
        } else if (this.origins$currentlyLoadedShader != null) {
            this.clearPostEffect();
            this.origins$currentlyLoadedShader = null;
        }
    }

    @Unique
    private static Identifier origins$normalizePostEffectId(Identifier id) {
        String path = id.getPath();
        if (path.startsWith("shaders/post/")) path = path.substring("shaders/post/".length());
        if (path.startsWith("post_effect/")) path = path.substring("post_effect/".length());
        if (path.endsWith(".json")) path = path.substring(0, path.length() - 5);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), path);
    }

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    private void origins$applyShaderAfterCameraChange(@Nullable Entity entity, CallbackInfo ci) {
        this.origins$currentlyLoadedShader = null;
        this.origins$updateShaderPower();
    }

    @Inject(method = "togglePostEffect", at = @At("HEAD"), cancellable = true)
    private void origins$disableShaderToggle(CallbackInfo ci) {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity != null && OriginDataHolder.get(cameraEntity).streamActivePowers(ShaderPower.class)
                .anyMatch(power -> !power.isToggleable() && Objects.equals(origins$normalizePostEffectId(power.getShader()), this.postEffectId))) {
            ci.cancel();
        }
    }

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void origins$nightVisionScale(LivingEntity living, float partialTick, CallbackInfoReturnable<Float> cir) {
        if (!living.hasEffect(MobEffects.NIGHT_VISION)) {
            OriginDataHolder.get(living).streamActivePowers(NightVisionPower.class)
                    .map(NightVisionPower::getStrength)
                    .max(Float::compareTo)
                    .ifPresent(cir::setReturnValue);
        }
    }
}
