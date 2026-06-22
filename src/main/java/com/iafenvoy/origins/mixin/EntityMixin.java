package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyVelocityPower;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
        if (OriginDataHolder.get(this.origins$self()).streamActivePowers(FireImmunityPower.class).findAny().isPresent())
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
        OriginDataHolder holder = OriginDataHolder.get(this.origins$self());
        return holder.streamActivePowers(ModifyVelocityPower.class).reduce(vec, (v, p) -> p.apply(holder, v), Vec3::add);
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At(value = "HEAD"), cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo ci) {
        if (PhasingPower.shouldPhaseThrough(this.origins$self(), BlockPos.containing(x, y, z))) ci.cancel();
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getOnPosLegacy()Lnet/minecraft/core/BlockPos;"))
    private void forceGrounded(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (OriginDataHolder.get(this.origins$self()).hasActivePower(GroundedPower.class)) {
            this.onGround = true;
        }
    }

    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean phantomInvisibility(boolean original) {
        return original || OriginDataHolder.get(this.origins$self()).hasActivePower(InvisibilityPower.class);
    }

    // 幻影穿墙: 不要从实体可以穿透的方块受到窒息（墙内）伤害。
    // 像原版 Entity#isInWall 一样重新扫描眼部区域，但忽略任何
    // PhasingPower 允许该实体穿透的窒息方块，因此仅对真正无法穿透的方块才认为"在墙内"。
    @ModifyReturnValue(method = "isInWall", at = @At("RETURN"))
    private boolean origins$noPhasingSuffocation(boolean original) {
        if (!original) return false;
        Entity self = this.origins$self();
        if (!OriginDataHolder.get(self).hasActivePower(PhasingPower.class)) return true;
        Level level = self.level();
        float checkWidth = self.getBbWidth() * 0.8F;
        AABB eyeBb = AABB.ofSize(self.getEyePosition(), checkWidth, 1.0E-6, checkWidth);
        return BlockPos.betweenClosedStream(eyeBb).anyMatch(pos -> {
            BlockState state = level.getBlockState(pos);
            return !state.isAir()
                    && state.isSuffocating(level, pos)
                    && Shapes.joinIsNotEmpty(state.getCollisionShape(level, pos).move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(eyeBb), BooleanOp.AND)
                    && !PhasingPower.shouldPhaseThrough(self, level, pos.immutable());
        });
    }
}
