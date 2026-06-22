package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.GlowPowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.EntityGlowPower;
import com.iafenvoy.origins.data.power.builtin.regular.SelfGlowPower;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(Entity.class)
public class Entity$ClientMixin {
    @Unique
    private Entity origins$self() {
        return (Entity) (Object) this;
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void handleGlowColor(CallbackInfoReturnable<Integer> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Entity entity = this.origins$self();
        Stream.concat(
                OriginDataHolder.get(player).streamActivePowers(EntityGlowPower.class),
                OriginDataHolder.get(entity).streamActivePowers(SelfGlowPower.class)
        ).filter(power -> !power.shouldUseTeam() && power.canGlow(player, entity)).mapToInt(GlowPowerHelper::getColor).forEach(cir::setReturnValue);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void handleGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = this.origins$self();
        if (!entity.level().isClientSide()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Stream.concat(
                OriginDataHolder.get(player).streamActivePowers(EntityGlowPower.class),
                OriginDataHolder.get(entity).streamActivePowers(SelfGlowPower.class)
        ).anyMatch(power -> power.canGlow(player, entity)))
            cir.setReturnValue(true);
    }
}
