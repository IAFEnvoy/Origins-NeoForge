package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.util.WaterBreathingHelper;
import com.iafenvoy.origins.data.power.builtin.regular.WaterBreathingPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class WaterBreathingMixin {
    @Mixin(LivingEntity.class)
    public static abstract class BreathingImpl extends Entity {
        private BreathingImpl(EntityType<?> type, Level level) {
            super(type, level);
        }

        @ModifyReturnValue(method = "canBreatheUnderwater", at = @At("RETURN"))
        private boolean origins$breatheUnderwater(boolean original) {
            return original
                    || !OriginDataHolder.get(this).getPowers(RegularPowers.WATER_BREATHING, WaterBreathingPower.class).isEmpty();
        }

        @Inject(method = "baseTick", at = @At("TAIL"))
        private void origins$waterBreathingTick(CallbackInfo ci) {
            WaterBreathingHelper.tick((LivingEntity) (Object) this);
        }
    }

    @Mixin(Player.class)
    public static abstract class TurtleHelmetProxy extends LivingEntity {
        private TurtleHelmetProxy(EntityType<? extends LivingEntity> entityType, Level level) {
            super(entityType, level);
        }

        @ModifyExpressionValue(method = "turtleHelmetTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
        private boolean origins$submergedProxy(boolean original) {
            return OriginDataHolder.get(this).getPowers(RegularPowers.WATER_BREATHING, WaterBreathingPower.class).isEmpty() == original;
        }
    }
}
