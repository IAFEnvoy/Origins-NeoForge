package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.event.common.CanClimbEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CommonHooks.class)
public class CommonHooksMixin {
    @Inject(method = "isLivingOnLadder", remap = false, at = @At("RETURN"), cancellable = true)
    private static void ladder(BlockState state, Level world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Optional<BlockPos>> info) {
        if (info.getReturnValue().isEmpty() && NeoForge.EVENT_BUS.post(new CanClimbEvent(entity)).getResult().allow())
            info.setReturnValue(Optional.of(pos));
    }
}