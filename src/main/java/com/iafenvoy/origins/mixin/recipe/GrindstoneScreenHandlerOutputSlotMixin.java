package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerModifiedGrindstone;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import com.iafenvoy.origins.event.OriginsModifierCollectEvent;
import com.iafenvoy.origins.util.math.Modifier;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(targets = "net/minecraft/world/inventory/GrindstoneMenu$4")
public abstract class GrindstoneScreenHandlerOutputSlotMixin {
    @Shadow
    @Final
    GrindstoneMenu this$0;

    @ModifyReturnValue(method = "getExperienceAmount", at = @At("RETURN"))
    private int origins$modifyExperience(int original, Level world) {
        if (!(this.this$0 instanceof PowerModifiedGrindstone powerModifiedGrindstone)) return original;
        List<Modifier> modifiers = powerModifiedGrindstone.origins$getAppliedPowers()
                .stream()
                .map(ModifyGrindstonePower::getXpModifier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        NeoForge.EVENT_BUS.post(new OriginsModifierCollectEvent(powerModifiedGrindstone.origins$getPlayer(), ModifyGrindstonePower.class, original, modifiers));
        return Modifier.applyModifiers(OriginDataHolder.get(powerModifiedGrindstone.origins$getPlayer()), modifiers, original);
    }
}
