package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.data.power.builtin.regular.ClimbingPower;
import com.iafenvoy.origins.event.client.ClientShouldGlowingEvent;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.iafenvoy.origins.event.common.CanStandOnFluidEvent;
import com.iafenvoy.origins.event.common.EntityFrozenEvent;
import com.iafenvoy.origins.event.common.IgnoreWaterEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
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

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void modifyWalkableFluids(FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (NeoForge.EVENT_BUS.post(new CanStandOnFluidEvent(this.origins$self(), fluidState)).getResult().allow())
            cir.setReturnValue(true);
    }

    // CLIMBING
    @ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
    private boolean handleClimbing(boolean original) {
        if (original) return true;
        List<ClimbingPower> climbingPowers = OriginDataHolder.get(this).getPowers(RegularPowers.CLIMBING, ClimbingPower.class);
        if (this.isSpectator() || climbingPowers.isEmpty()) return false;
        this.lastClimbablePos = Optional.of(this.blockPosition());
        return true;
    }

    @ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
    private boolean handleClimbingHold(boolean original) {
        List<ClimbingPower> climbingPowers = OriginDataHolder.get(this).getPowers(RegularPowers.CLIMBING, ClimbingPower.class);
        if (climbingPowers.isEmpty()) return original;
        return climbingPowers.stream().anyMatch(x -> x.canHold(this));
    }
}
