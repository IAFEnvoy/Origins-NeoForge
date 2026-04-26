package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyExhaustionPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.iafenvoy.origins.event.common.CanNaturalRegenEvent;
import com.iafenvoy.origins.util.Mutable;
import com.iafenvoy.origins.util.math.Modifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Unique
    private Player origins$self() {
        return (Player) (Object) this;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean checkNaturalSpawn(boolean original) {
        return original && NeoForge.EVENT_BUS.post(new CanNaturalRegenEvent(this.origins$self())).getResult().allow();
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void handleElytra(CallbackInfoReturnable<Boolean> cir) {
        Player player = this.origins$self();
        if (!player.onGround() && !player.isFallFlying() && !player.isInWater()) {
            if (NeoForge.EVENT_BUS.post(new CanFlyWithoutElytraEvent(player)).getResult().allow()) {
                player.startFallFlying();
                cir.setReturnValue(true);
            }
        }
    }

    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, name = "exhaustion", argsOnly = true)
    private float modifyExhaustion(float exhaustion) {
        return OriginDataHolder.get(this.origins$self()).streamActivePowers(ModifyExhaustionPower.class).map(ModifyExhaustionPower::getModifiers).reduce(exhaustion, (p, c) -> (float) Modifier.applyModifiers(c, p), Float::sum);
    }

    @ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
    private ItemStack modifyEatenItemStack(ItemStack original) {
        Mutable<ItemStack> stack = Mutable.of(original.copy());
        ModifyFoodPower.modifyStack(this.origins$self().level(), this.origins$self(), stack);
        return stack.get();
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER))
    private void afterEatToFoodData(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
        Entity entity = this.origins$self();
        OriginDataHolder.get(entity).streamActivePowers(ModifyFoodPower.class).filter(x -> x.getItemCondition().test(level, food)).map(ModifyFoodPower::getEntityAction).forEach(x -> x.execute(entity));
    }
}
