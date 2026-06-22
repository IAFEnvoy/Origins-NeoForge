package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Consumable.class)
public abstract class ConsumableMixin {
    @ModifyVariable(method = "onConsume", at = @At("HEAD"), argsOnly = true)
    private ItemStack origins$modifyConsumedStack(ItemStack original, Level level, LivingEntity user) {
        Mutable.Stack stack = Mutable.stack(original);
        ModifyFoodPower.modifyStack(level, user, stack);
        return stack.get();
    }

    @Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void origins$afterFoodApplied(Level level, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        OriginDataHolder.get(user).streamActivePowers(ModifyFoodPower.class)
                .filter(power -> power.getItemCondition().test(level, stack))
                .map(ModifyFoodPower::getEntityAction)
                .forEach(action -> action.execute(user));
    }

    @ModifyExpressionValue(method = "onConsume", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/component/Consumable;onConsumeEffects:Ljava/util/List;"))
    private List<ConsumeEffect> origins$preventFoodEffects(List<ConsumeEffect> original, Level level, LivingEntity user, ItemStack stack) {
        return OriginDataHolder.get(user).streamActivePowers(ModifyFoodPower.class)
                .anyMatch(ModifyFoodPower::shouldPreventEffects) ? List.of() : original;
    }

}
