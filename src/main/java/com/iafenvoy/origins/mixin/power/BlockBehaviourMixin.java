package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyBreakSpeedPower;
import com.iafenvoy.origins.util.math.Modifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    /*
    This is done exclusively when the destroyProgress is 0.0 or less, which handles breaking blocks
    that are unbreakable. This method is the same as Origins Fabric.

    I don't think that I'm able to handle it within the forge event, so it has to go here instead.
     */
    @Inject(method = "getDestroyProgress", at = @At(value = "RETURN"), cancellable = true)
    private void allowUnbreakableBreaking(BlockState state, Player player, BlockGetter getter, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (state.getDestroySpeed(getter, pos) <= 0)
            cir.setReturnValue(OriginDataHolder.get(player)
                    .streamActivePowers(ModifyBreakSpeedPower.class)
                    .filter(x -> x.getBlockCondition().test(player.level(), pos))
                    .map(ModifyBreakSpeedPower::getModifier)
                    .reduce(cir.getReturnValue(), (cur, mod) -> (float) Modifier.applyModifiers(mod, cur), Float::sum));
    }
}
