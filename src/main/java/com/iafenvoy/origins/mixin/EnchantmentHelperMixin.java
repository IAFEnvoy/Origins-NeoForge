package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sets entity context on ItemStack before enchantment level queries,
 * so that {@link com.iafenvoy.origins.data.power.builtin.modify.ModifyEnchantmentLevelPower}
 * can read the entity from EntityLinkedItemStack during GetEnchantmentLevelEvent.
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("HEAD"))
    private static void origins$setEntityOnStackForLevelQuery(Holder<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        for (EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) ((EntityLinkedItemStack) (Object) stack).origins$setEntity(entity);
        }
    }
}
