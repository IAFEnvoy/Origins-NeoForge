package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.client.ClientShouldGlowingEvent;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.iafenvoy.origins.event.common.EntityFrozenEvent;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private LivingEntity origins$self() {
        return (LivingEntity) (Object) this;
    }

    @ModifyExpressionValue(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack handleElytra(ItemStack original) {
        return this.origins$self() instanceof Player player && NeoForge.EVENT_BUS.post(new CanFlyWithoutElytraEvent(player)).getResult().allow() ? Items.ELYTRA.getDefaultInstance() : original;
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
}
