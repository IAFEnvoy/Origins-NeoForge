package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.client.ClientGlowingColorEvent;
import com.iafenvoy.origins.event.common.EntityFireImmuneEvent;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@OnlyIn(Dist.CLIENT)
@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private Entity origins$self() {
        return (Entity) (Object) this;
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void handleGlowColor(CallbackInfoReturnable<Integer> cir) {
        OptionalInt color = NeoForge.EVENT_BUS.post(new ClientGlowingColorEvent(this.origins$self())).getColor();
        if (color.isPresent()) cir.setReturnValue(color.getAsInt());
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void handleFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (NeoForge.EVENT_BUS.post(new EntityFireImmuneEvent(this.origins$self())).getResult().allow())
            cir.setReturnValue(true);
    }
}
