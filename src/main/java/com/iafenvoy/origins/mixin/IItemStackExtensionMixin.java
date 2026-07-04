package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.ConditionedRestrictArmorPower;
import com.iafenvoy.origins.data.power.builtin.regular.EdibleItemPower;
import com.iafenvoy.origins.data.power.builtin.regular.ElytraFlightPower;
import com.iafenvoy.origins.data.power.builtin.regular.RestrictArmorPower;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
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
public interface IItemStackExtensionMixin {
    @SuppressWarnings("ShadowModifiers")
    @Shadow
    ItemStack self();

    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventArmorEquip(EquipmentSlot armorType, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.self();
        PowerHelper helper = PowerHelper.get(entity);
        if (Stream.concat(
                helper.streamActive(ConditionedRestrictArmorPower.class).map(ConditionedRestrictArmorPower::getConditions),
                helper.streamActive(RestrictArmorPower.class).map(RestrictArmorPower::getConditions)
        ).anyMatch(x -> x.get(armorType) != null && x.get(armorType).test(entity.level(), stack)) ||
                stack.is(Items.ELYTRA) && helper.anyActive(ElytraFlightPower.class))
            cir.setReturnValue(false);
    }

    @Inject(method = "getFoodProperties", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifyEdibleItem(LivingEntity entity, CallbackInfoReturnable<FoodProperties> cir) {
        ItemStack stack = this.self();
        EdibleItemPower.get(stack, entity).ifPresent(power -> {
            cir.setReturnValue(power.getFoodProperties());
        });
    }
}
