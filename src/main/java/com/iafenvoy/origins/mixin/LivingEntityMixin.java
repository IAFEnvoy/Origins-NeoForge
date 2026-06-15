package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.*;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventEntityCollisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.iafenvoy.origins.mixin.accessor.MobEffectInstanceAccessor;
import com.iafenvoy.origins.util.WaterBreathingHelper;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
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

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"), name = "d0")
    private double modifyFalling(double d0) {
        return this.getDeltaMovement().y > 0 ? d0 : ModifyFallingPower.apply(this.origins$self(), d0);
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 0))
    private double handleSpeedInWater(double original) {
        return OriginDataHolder.get(this.origins$self()).streamActivePowers(IgnoreWaterPower.class).findAny().isPresent() ? 1 : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
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

    @ModifyVariable(method = "eat*", at = @At("HEAD"), argsOnly = true)
    private ItemStack modifyEatenItemStack(ItemStack original) {
        if (this.origins$self() instanceof Player) return original;
        Mutable.Stack stack = Mutable.stack(original);
        ModifyFoodPower.modifyStack(this.level(), this.origins$self(), stack);
        return stack.get();
    }

    @WrapWithCondition(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"))
    private boolean preventApplyingFoodEffects(LivingEntity instance, FoodProperties foodProperties) {
        return OriginDataHolder.get(instance).streamActivePowers(ModifyFoodPower.class).noneMatch(ModifyFoodPower::shouldPreventEffects);
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
}
