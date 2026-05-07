package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.GlowPowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyVelocityPower;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

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

    @Redirect(method = "lambda$isInWall$8", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape preventSuffocation(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getCollisionShape(level, pos, CollisionContext.of(this.origins$self()));
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getOnPosLegacy()Lnet/minecraft/core/BlockPos;"))
    private void forceGrounded(MoverType pType, Vec3 pPos, CallbackInfo ci) {
        if (OriginDataHolder.get(this.origins$self()).hasActivePower(GroundedPower.class)) {
            this.onGround = true;
        }
    }

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void phantomInvisibility(CallbackInfoReturnable<Boolean> info) {
        if (OriginDataHolder.get(this.origins$self()).hasActivePower(InvisibilityPower.class))
            info.setReturnValue(true);
    }

    @OnlyIn(Dist.CLIENT)
    @Mixin(Entity.class)
    public static class Client {
        @Unique
        private Entity origins$self() {
            return (Entity) (Object) this;
        }

        @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
        private void handleGlowColor(CallbackInfoReturnable<Integer> cir) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            Entity entity = this.origins$self();
            Stream.concat(
                    OriginDataHolder.get(player).streamActivePowers(EntityGlowPower.class),
                    OriginDataHolder.get(entity).streamActivePowers(SelfGlowPower.class)
            ).filter(power -> !power.shouldUseTeam() && power.canGlow(player, entity)).mapToInt(GlowPowerHelper::getColor).forEach(cir::setReturnValue);
        }
    }
}
