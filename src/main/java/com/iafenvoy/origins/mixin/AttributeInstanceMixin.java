package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.AttributeInstanceAccessor;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFallingPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AttributeInstance.class)
public class AttributeInstanceMixin implements AttributeInstanceAccessor {
    @Shadow
    @Final
    private Attribute attribute;

    @Unique
    @Nullable
    private LivingEntity apoli$entity;

    @Override
    public void origins$setEntity(LivingEntity entity) {
        this.apoli$entity = entity;
    }

    @Override
    public @Nullable LivingEntity origins$getEntity() {
        return this.apoli$entity;
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void apoli$modifyAttributeValue(CallbackInfoReturnable<Double> cir) {
        if (this.apoli$entity != null && this.attribute == Attributes.GRAVITY.value() && this.apoli$entity.getDeltaMovement().y <= 0 && OriginDataHolder.get(this.apoli$entity).hasPower(ModifyFallingPower.class, true)) {
            double original = cir.getReturnValueD();
            cir.setReturnValue(ModifyFallingPower.apply(this.apoli$entity, original));
        }
    }
}
