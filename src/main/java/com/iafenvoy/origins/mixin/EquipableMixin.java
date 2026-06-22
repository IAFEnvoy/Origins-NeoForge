package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ConditionedRestrictArmorPower;
import com.iafenvoy.origins.data.power.builtin.regular.RestrictArmorPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// 26.1: Equipable 接口被 Equippable 组件替换。重新定位目标以使其能够编译；
// 注入点/局部变量在游戏内验证后可能需要重新检查。
@Mixin(Equippable.class)
public abstract class EquipableMixin {
    @Shadow
    public abstract EquipmentSlot slot();

    @ModifyExpressionValue(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canUseSlot(Lnet/minecraft/world/entity/EquipmentSlot;)Z"))
    private boolean preventArmorEquipping(boolean original, ItemStack stack, Player player) {
        Level level = player.level();
        EquipmentSlot slot = this.slot();
        OriginDataHolder holder = OriginDataHolder.get(player);
        return original
                && holder.getHelper().holder().streamActivePowers(ConditionedRestrictArmorPower.class).noneMatch(p -> p.getConditions().get(slot).test(level, stack))
                && holder.getHelper().holder().streamActivePowers(RestrictArmorPower.class).noneMatch(p -> p.getConditions().get(slot).test(level, stack));
    }
}
