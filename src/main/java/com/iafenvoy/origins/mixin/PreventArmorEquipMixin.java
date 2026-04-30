package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ConditionedRestrictArmorPower;
import com.iafenvoy.origins.data.power.builtin.regular.ElytraFlightPower;
import com.iafenvoy.origins.data.power.builtin.regular.RestrictArmorPower;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(IItemStackExtension.class)
public interface PreventArmorEquipMixin {
    @SuppressWarnings("ShadowModifiers")
    @Shadow
    ItemStack self();

    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true, remap = false)
    private void apoli$preventArmorEquip(EquipmentSlot armorType, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        OriginDataHolder holder = OriginDataHolder.get(entity);
        ItemStack stack = this.self();
        if (Stream.concat(
                holder.streamActivePowers(ConditionedRestrictArmorPower.class).map(ConditionedRestrictArmorPower::getConditions),
                holder.streamActivePowers(RestrictArmorPower.class).map(RestrictArmorPower::getConditions)
        ).anyMatch(x -> x.get(armorType) != null && x.get(armorType).test(entity.level(), stack)) ||
                stack.is(Items.ELYTRA) && holder.hasPower(ElytraFlightPower.class, true))
            cir.setReturnValue(false);
    }
}
