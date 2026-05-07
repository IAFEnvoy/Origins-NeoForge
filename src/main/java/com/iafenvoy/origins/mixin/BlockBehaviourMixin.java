package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyBreakSpeedPower;
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
    /*
    This is done exclusively when the destroyProgress is 0.0 or less, which handles breaking blocks
    that are unbreakable. This method is the same as Origins Fabric.

    I don't think that I'm able to handle it within the forge event, so it has to go here instead.
     */
    @ModifyReturnValue(method = "getDestroyProgress", at = @At(value = "RETURN"))
    private float allowUnbreakableBreaking(float original, BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        if (state.getDestroySpeed(getter, pos) <= 0)
            return OriginDataHolder.get(player).getHelper().modify(ModifyBreakSpeedPower.class, p -> p.getBlockCondition().test(player.level(), pos), original);
        return original;
    }
}
