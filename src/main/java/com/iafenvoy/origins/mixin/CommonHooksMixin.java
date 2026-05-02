package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ClimbingPower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CommonHooks.class)
public class CommonHooksMixin {
    @Inject(method = "isLivingOnLadder", at = @At("RETURN"), cancellable = true)
    private static void ladder(BlockState state, Level level, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Optional<BlockPos>> info) {
        if (info.getReturnValue().isEmpty() && OriginDataHolder.get(entity).streamActivePowers(ClimbingPower.class).findAny().isPresent())
            info.setReturnValue(Optional.of(pos));
    }
}