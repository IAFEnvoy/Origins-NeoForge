package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.accessor.MovingEntity;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyVelocityPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.iafenvoy.origins.event.client.ClientGlowingColorEvent;
import com.iafenvoy.origins.event.common.EntityFireImmuneEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@OnlyIn(Dist.CLIENT)
@Mixin(Entity.class)
public class EntityMixin implements MovingEntity {
    @Shadow
    public float moveDist;
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

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void handleGlowColor(CallbackInfoReturnable<Integer> cir) {
        OptionalInt color = NeoForge.EVENT_BUS.post(new ClientGlowingColorEvent(this.origins$self())).getColor();
        if (color.isPresent()) cir.setReturnValue(color.getAsInt());
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void handleFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (NeoForge.EVENT_BUS.post(new EntityFireImmuneEvent(this.origins$self())).getResult().allow())
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
        return OriginDataHolder.get(this.origins$self()).streamActivePowers(ModifyVelocityPower.class).reduce(vec, (v, p) -> p.apply(v), Vec3::add);
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At(value = "HEAD"), cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo ci) {
        if (PhasingPower.shouldPhaseThrough(this.origins$self(), BlockPos.containing(x, y, z))) ci.cancel();
    }

    @Redirect(method = "lambda$isInWall$8", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape apoli$preventSuffocation(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getCollisionShape(level, pos, CollisionContext.of(this.origins$self()));
    }
}
