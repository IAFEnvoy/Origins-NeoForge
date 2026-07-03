package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyVelocityPower;
import com.iafenvoy.origins.data.power.builtin.regular.FireImmunityPower;
import com.iafenvoy.origins.data.power.builtin.regular.GroundedPower;
import com.iafenvoy.origins.data.power.builtin.regular.InvisibilityPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements MovingEntity {
    @Shadow
    public float moveDist;
    @Shadow
    private boolean onGround;
    @Unique
    private boolean origins$isMoving;
    @Unique
    private float origins$distanceBefore;

    @Unique
    private Entity origins$self() {
        return (Entity) (Object) this;
    }

    @Override
    public boolean origins$isMoving() {
        return this.origins$isMoving;
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void handleFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHelper.get(this.origins$self()).anyActive(FireImmunityPower.class, x -> true))
            cir.setReturnValue(true);
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void saveDistanceTraveled(MoverType type, Vec3 movement, CallbackInfo ci) {
        this.origins$isMoving = false;
        this.origins$distanceBefore = this.moveDist;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void checkIsMoving(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (this.moveDist > this.origins$distanceBefore)
            this.origins$isMoving = true;
    }

    @ModifyVariable(method = "move", at = @At(value = "HEAD"), argsOnly = true)
    private Vec3 modifyMovementVelocityXZ(Vec3 vec, MoverType movementType) {
        if (movementType != MoverType.SELF) return vec;
        return PowerHelper.get(this.origins$self()).reduce(ModifyVelocityPower.class, vec, (h, v, p) -> p.apply(h, v), Vec3::add);
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At(value = "HEAD"), cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo ci) {
        if (PhasingPower.shouldPhaseThrough(this.origins$self(), BlockPos.containing(x, y, z))) ci.cancel();
    }

    @Redirect(method = "lambda$isInWall$8", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape preventSuffocation(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getCollisionShape(level, pos, CollisionContext.of(this.origins$self()));
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getOnPosLegacy()Lnet/minecraft/core/BlockPos;"))
    private void forceGrounded(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (PowerHelper.get(this.origins$self()).anyActive(GroundedPower.class)) {
            this.onGround = true;
        }
    }

    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean phantomInvisibility(boolean original) {
        return original || PowerHelper.get(this.origins$self()).anyActive(InvisibilityPower.class);
    }
}
