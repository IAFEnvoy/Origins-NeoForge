package com.iafenvoy.origins.mixin.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventBlockSelectionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Unique
    private boolean origins$isAbove(Entity entity, VoxelShape shape, BlockPos pos) {
        return entity.getY() > (double) pos.getY() + shape.max(Direction.Axis.Y) - (entity.onGround() ? 8.05 / 16.0 : 0.0015);
    }

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void preventCollisionWhenPhasing(Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && PhasingPower.shouldPhaseThrough(living, pos))
            ci.cancel();
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void phaseThroughBlocks(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> info) {
        VoxelShape blockShape = info.getReturnValue();
        if (!blockShape.isEmpty() && context instanceof EntityCollisionContext esc) {
            if (esc.getEntity() != null) {
                Entity entity = esc.getEntity();
                boolean isAbove = this.origins$isAbove(entity, blockShape, pos);
                if (world instanceof Level level && PhasingPower.shouldPhaseThrough(entity, level, pos, isAbove))
                    info.setReturnValue(Shapes.empty());
            }
        }
    }

    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void modifyBlockOutline(BlockGetter getter, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (getter instanceof Level level && context instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
            Entity entity = ctx.getEntity();
            if (OriginDataHolder.get(entity).streamActivePowers(PreventBlockSelectionPower.class).anyMatch(x -> x.getBlockCondition().test(level, pos)))
                cir.setReturnValue(Shapes.empty());
        }
    }
}
