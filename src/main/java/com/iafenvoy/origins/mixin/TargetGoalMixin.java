package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.ModifyTargetRangePower;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public abstract class TargetGoalMixin {
    @Shadow
    @Final
    protected Mob mob;

    @Inject(method = "getFollowDistance", at = @At("RETURN"), cancellable = true)
    private void origins$expandTargetSearchForPowers(CallbackInfoReturnable<Double> cir) {
        double range = cir.getReturnValue();
        AABB search = this.mob.getBoundingBox().inflate(256);
        for (Player player : this.mob.level().getEntitiesOfClass(Player.class, search, Player::isAlive))
            range = Math.max(range, ModifyTargetRangePower.modify(this.mob, player, cir.getReturnValue()));
        cir.setReturnValue(range);
    }
}
