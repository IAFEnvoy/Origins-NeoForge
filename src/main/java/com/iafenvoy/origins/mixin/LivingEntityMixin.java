package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.AttributeInstanceAccessor;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyAirSpeedPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyEffectAmplifierPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyEffectDurationPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventEntityCollisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.ClimbingPower;
import com.iafenvoy.origins.data.power.builtin.regular.LikeWaterPower;
import com.iafenvoy.origins.data.power.builtin.regular.WalkOnFluidPower;
import com.iafenvoy.origins.event.client.ClientShouldGlowingEvent;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.iafenvoy.origins.event.common.EntityFrozenEvent;
import com.iafenvoy.origins.event.common.IgnoreWaterEvent;
import com.iafenvoy.origins.mixin.accessor.MobEffectInstanceAccessor;
import com.iafenvoy.origins.util.Mutable;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<BlockPos> lastClimbablePos;

    @Shadow
    protected abstract float getFlyingSpeed();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private LivingEntity origins$self() {
        return (LivingEntity) (Object) this;
    }

    @Inject(method = "getAttribute", at = @At("RETURN"))
    private void setEntityToAttributeInstance(Holder<Attribute> attribute, CallbackInfoReturnable<AttributeInstance> cir) {
        AttributeInstance instance = cir.getReturnValue();
        if (instance != null) ((AttributeInstanceAccessor) instance).origins$setEntity(this.origins$self());
    }

    @ModifyExpressionValue(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack handleElytra(ItemStack original) {
        return NeoForge.EVENT_BUS.post(new CanFlyWithoutElytraEvent(this.origins$self())).getResult().allow() ? Items.ELYTRA.getDefaultInstance() : original;
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void handleGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (NeoForge.EVENT_BUS.post(new ClientShouldGlowingEvent(this.origins$self())).getResult().allow())
            cir.setReturnValue(true);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
    private void handleFrozen(CallbackInfo ci) {
        if (NeoForge.EVENT_BUS.post(new EntityFrozenEvent(this.origins$self())).getResult().allow())
            this.isInPowderSnow = true;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal = 0))
    private double handleSpeedInWater(double original) {
        return NeoForge.EVENT_BUS.post(new IgnoreWaterEvent(this.origins$self())).getResult().allow() ? 1 : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 origins$modifyFluidMovement(Vec3 original, @Local(ordinal = 0) double fallVelocity) {
        List<LikeWaterPower> powers = OriginDataHolder.get(this).getPowers(RegularPowers.LIKE_WATER, LikeWaterPower.class);
        if (!powers.isEmpty() && Math.abs(original.y - fallVelocity / 16.0D) < 0.025D) {
            return new Vec3(original.x, 0, original.z);
        }
        return original;
    }

    @ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
    private boolean handleClimbing(boolean original) {
        if (original) return true;
        if (this.isSpectator() || !OriginDataHolder.get(this).isPowerActive(ClimbingPower.class)) return false;
        this.lastClimbablePos = Optional.of(this.blockPosition());
        return true;
    }

    @ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
    private boolean handleClimbingHold(boolean original) {
        OriginDataHolder holder = OriginDataHolder.get(this);
        return original || holder.streamActivePowers(ClimbingPower.class).anyMatch(x -> x.isActive(holder) && x.canHold(this));
    }

    @ModifyVariable(method = "eat*", at = @At("HEAD"), argsOnly = true)
    private ItemStack modifyEatenItemStack(ItemStack original) {
        if (this.origins$self() instanceof Player) return original;
        Mutable<ItemStack> stack = Mutable.of(original.copy());
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

    @Inject(method = "getFrictionInfluencedSpeed(F)F", at = @At("RETURN"), cancellable = true)
    private void modifyFlySpeed(float slipperiness, CallbackInfoReturnable<Float> cir) {
        if (!this.onGround())
            cir.setReturnValue(OriginDataHolder.get(this.origins$self()).getHelper().modify(ModifyAirSpeedPower.class, this.getFlyingSpeed()));
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
}
