package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    // 26.1: Item#use 现在返回 InteractionResult。重新定位到 HEAD，因为旧的
    // InteractionResultHolder.fail() 调用不再存在于方法体中。
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void tryItemAlwaysEdible(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (OriginDataHolder.get(user).streamActivePowers(ModifyFoodPower.class).filter(x -> x.getItemCondition().test(world, itemStack)).anyMatch(ModifyFoodPower::isAlwaysEdible)) {
            user.startUsingItem(hand);
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
