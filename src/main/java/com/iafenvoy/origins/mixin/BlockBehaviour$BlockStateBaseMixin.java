package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventBlockSelectionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviour$BlockStateBaseMixin {
    @Unique
    private boolean origins$isAbove(Entity entity, VoxelShape shape, BlockPos pos) {
        return entity.getY() > (double) pos.getY() + shape.max(Direction.Axis.Y) - (entity.onGround() ? 8.05 / 16.0 : 0.0015);
    }

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void preventCollisionWhenPhasing(Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && PhasingPower.shouldPhaseThrough(living, pos))
            ci.cancel();
    }

    @ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape phaseThroughBlocks(VoxelShape original, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (!original.isEmpty() && context instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
            Entity entity = ctx.getEntity();
            if (getter instanceof Level level && PhasingPower.shouldPhaseThrough(entity, level, pos, this.origins$isAbove(entity, original, pos)))
                return Shapes.empty();
        }
        return original;
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape modifyBlockOutline(VoxelShape original, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (getter instanceof Level level && context instanceof EntityCollisionContext ctx && ctx.getEntity() != null && PowerHelper.get(ctx.getEntity()).anyActive(PreventBlockSelectionPower.class, x -> x.getBlockCondition().test(level, pos)))
            return Shapes.empty();
        return original;
    }
}
