package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyBreakSpeedPower;
import com.iafenvoy.origins.util.math.Modifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float modifyBlockHardness(float original, BlockState state, Player player, BlockGetter world, BlockPos pos) {
        OriginDataHolder holder = OriginDataHolder.get(player);
        return Math.max(Modifier.applyModifiers(holder, holder.streamActivePowers(ModifyBreakSpeedPower.class)
                .filter(p -> p.getBlockCondition().test(player.level(), pos))
                .flatMap(p -> p.getHardnessModifier().stream())
                .toList(), original), -1.0F);
    }

    @ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
    private float modifyBlockBreakSpeed(float original, BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        return OriginDataHolder.get(player).getHelper().modify(ModifyBreakSpeedPower.class, p -> p.getBlockCondition().test(player.level(), pos), original);
    }
}
