package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFoodPower;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;fail(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
    private void tryItemAlwaysEdible(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (OriginDataHolder.get(user).streamActivePowers(ModifyFoodPower.class).filter(x -> x.getItemCondition().test(world, itemStack)).anyMatch(ModifyFoodPower::isAlwaysEdible)) {
            user.startUsingItem(hand);
            cir.setReturnValue(InteractionResultHolder.consume(itemStack));
        }
    }
}
