package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyAirSpeedPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyExhaustionPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import com.iafenvoy.origins.data.power.builtin.regular.DisableRegenPower;
import com.iafenvoy.origins.data.power.builtin.regular.WaterBreathingPower;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
        return original && PowerHelper.get(this.origins$self()).noneActive(DisableRegenPower.class);
    }

    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, name = "exhaustion", argsOnly = true)
    private float modifyExhaustion(float exhaustion) {
        return PowerHelper.get(this.origins$self()).modify(ModifyExhaustionPower.class, exhaustion);
    }

    @ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
    private ItemStack modifyEatenItemStack(ItemStack original) {
        Mutable.Stack stack = Mutable.stack(original);
        ModifyFoodPower.modifyStack(this.origins$self().level(), this.origins$self(), stack);
        return stack.get();
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER))
    private void afterEatToFoodData(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
        Entity entity = this.origins$self();
        PowerHelper.get(entity).execute(ModifyFoodPower.class, x -> x.getItemCondition().test(level, food), (h, p) -> p.getEntityAction().execute(entity));
    }

    @WrapOperation(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void origins$modifyEatenFoodProperties(FoodData instance, FoodProperties foodProperties, Operation<Void> original, @Local(argsOnly = true) ItemStack food) {
        original.call(instance, ModifyFoodPower.modifyFoodProperties(this.origins$self().level(), this.origins$self(), food, foodProperties));
    }

    @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
    private float modifyFlySpeed(float original) {
        return PowerHelper.get(this.origins$self()).modify(ModifyAirSpeedPower.class, original);
    }

    @ModifyExpressionValue(method = "turtleHelmetTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean origins$submergedProxy(boolean original) {
        return original ^ PowerHelper.get(this.origins$self()).anyActive(WaterBreathingPower.class);
    }
}
