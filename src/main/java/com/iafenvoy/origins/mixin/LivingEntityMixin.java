package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.*;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventEntityCollisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.iafenvoy.origins.mixin.accessor.MobEffectInstanceAccessor;
import com.iafenvoy.origins.util.WaterBreathingHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<BlockPos> lastClimbablePos;

    @Shadow
    protected abstract boolean canGlide();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private LivingEntity origins$self() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
    private void handleFrozen(CallbackInfo ci) {
        if (OriginDataHolder.get(this.origins$self()).streamActivePowers(FreezePower.class).findAny().isPresent())
            this.isInPowderSnow = true;
    }

    @ModifyExpressionValue(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffectiveGravity()D"))
    private double modifyFallingInAir(double gravity) {
        return this.getDeltaMovement().y > 0 ? gravity : ModifyFallingPower.apply(this.origins$self(), gravity);
    }

    @ModifyExpressionValue(method = "travelInFluid(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/material/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffectiveGravity()D"))
    private double modifyFallingInFluid(double gravity) {
        return this.getDeltaMovement().y > 0 ? gravity : ModifyFallingPower.apply(this.origins$self(), gravity);
    }

    @ModifyExpressionValue(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 0))
    private double handleSpeedInWater(double original) {
        return OriginDataHolder.get(this.origins$self()).streamActivePowers(IgnoreWaterPower.class).findAny().isPresent() ? 1 : original;
    }

    @ModifyExpressionValue(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 origins$modifyFluidMovement(Vec3 original, @Local(ordinal = 0) double fallVelocity) {
        if (OriginDataHolder.get(this).hasActivePower(LikeWaterPower.class) && Math.abs(original.y - fallVelocity / 16.0D) < 0.025D)
            return new Vec3(original.x, 0, original.z);
        return original;
    }

    @ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
    private boolean handleClimbing(boolean original) {
        if (original) return true;
        if (this.isSpectator()) return false;
        OriginDataHolder holder = OriginDataHolder.get(this);
        if (holder.streamActivePowers(ClimbingPower.class).noneMatch(x -> x.canClimb(this)))
            return false;
        this.lastClimbablePos = Optional.of(this.blockPosition());
        return true;
    }

    @ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
    private boolean handleClimbingHold(boolean original) {
        OriginDataHolder holder = OriginDataHolder.get(this);
        return original || holder.streamActivePowers(ClimbingPower.class).anyMatch(x -> x.canHold(this));
    }

    @ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
    private MobEffectInstance modifyStatusEffect(MobEffectInstance effect) {
        Holder<MobEffect> effectType = effect.getEffect();
        int originalAmp = effect.getAmplifier();
        int originalDur = effect.getDuration();

        int amplifier = OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyEffectAmplifierPower.class, p -> p.doesApply(effectType), originalAmp);
        int duration = OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyEffectDurationPower.class, p -> p.doesApply(effectType), originalDur);

        if (amplifier != originalAmp || duration != originalDur)
            return new MobEffectInstance(effectType, duration, amplifier, effect.isAmbient(), effect.isVisible(), effect.showIcon(), ((MobEffectInstanceAccessor) effect).getHiddenEffect());
        return effect;
    }

    @ModifyExpressionValue(method = "getFrictionInfluencedSpeed(F)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFlyingSpeed()F"))
    private float modifyFlySpeed(float original) {
        return OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyAirSpeedPower.class, original);
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void modifyWalkableFluids(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        if (OriginDataHolder.get(this.origins$self()).streamActivePowers(WalkOnFluidPower.class).anyMatch(x -> fluid.is(x.getFluid())))
            cir.setReturnValue(true);
    }

    @Inject(method = "doPush", at = @At("HEAD"), cancellable = true)
    private void preventPushing(Entity target, CallbackInfo ci) {
        Entity self = this.origins$self();
        if (OriginDataHolder.get(self).streamActivePowers(PreventEntityCollisionPower.class).anyMatch(x -> x.getBiEntityCondition().test(self, target)) ||
                OriginDataHolder.get(target).streamActivePowers(PreventEntityCollisionPower.class).anyMatch(x -> x.getBiEntityCondition().test(target, self)))
            ci.cancel();
    }

    @ModifyReturnValue(method = "canBreatheUnderwater", at = @At("RETURN"))
    private boolean origins$breatheUnderwater(boolean original) {
        return original || OriginDataHolder.get(this).hasActivePower(WaterBreathingPower.class);
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void origins$waterBreathingTick(CallbackInfo ci) {
        WaterBreathingHelper.tick((LivingEntity) (Object) this);
    }

    // ElytraFlightPower 替代（26.1 不可用的）Caelus 滑翔飞行属性：
    // 在能量激活时报告可以滑翔，使原版在不使用鞘翅物品的情况下启动并维持滑翔。
    // 在双端运行，因此 LocalPlayer#tryToStartFallFlying（客户端）发送 START_FALL_FLYING 数据包，
    // 服务器保持玩家滑翔状态。
    @Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    private void origins$powerGlide(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = this.origins$self();
        if (!this.onGround() && !this.isPassenger() && !self.hasEffect(MobEffects.LEVITATION)
                && OriginDataHolder.get(self).hasActivePower(ElytraFlightPower.class))
            cir.setReturnValue(true);
    }

    // 当滑翔由能量授予且没有穿戴真正的滑翔物品时，原版的 updateFallFlying
    // 会在随机选择一个滑翔物品槽位进行耐久损坏时崩溃（Util.getRandom 对空列表操作）。
    // 执行安全的子集：保留坠落距离计算和地面/条件停止，但跳过耐久损坏。
    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void origins$powerFallFlying(CallbackInfo ci) {
        LivingEntity self = this.origins$self();
        if (!OriginDataHolder.get(self).hasActivePower(ElytraFlightPower.class)) return;
        for (EquipmentSlot slot : EquipmentSlot.VALUES)
            if (LivingEntity.canGlideUsing(self.getItemBySlot(slot), slot)) return;
        this.checkFallDistanceAccumulation();
        if (!this.level().isClientSide() && !this.canGlide())
            this.setSharedFlag(7, false);
        ci.cancel();
    }
}
