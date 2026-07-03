package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.ConditionedRestrictArmorPower;
import com.iafenvoy.origins.data.power.builtin.regular.RestrictArmorPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Equipable.class)
public interface EquipableMixin {
    @ModifyExpressionValue(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canUseSlot(Lnet/minecraft/world/entity/EquipmentSlot;)Z"))
    private boolean preventArmorEquipping(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) Player player, @Local ItemStack stack, @Local EquipmentSlot slot) {
        PowerHelper helper = PowerHelper.get(player);
        return original
                && helper.noneActive(ConditionedRestrictArmorPower.class, p -> p.getConditions().get(slot).test(level, stack))
                && helper.noneActive(RestrictArmorPower.class, p -> p.getConditions().get(slot).test(level, stack));
    }
}
