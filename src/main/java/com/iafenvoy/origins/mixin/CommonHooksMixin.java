package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.ClimbingPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
    @ModifyReturnValue(method = "isLivingOnLadder", at = @At("RETURN"))
    private static Optional<BlockPos> ladder(Optional<BlockPos> original, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return original.isEmpty() && OriginDataHolder.get(entity).streamActivePowers(ClimbingPower.class).findAny().isPresent() ? Optional.of(pos) : original;
    }
}