package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BlockCollisionCondition(float offsetX, float offsetY, float offsetZ) implements EntityCondition {
    public static final MapCodec<BlockCollisionCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("offset_x", 0F).forGetter(BlockCollisionCondition::offsetX),
            Codec.FLOAT.optionalFieldOf("offset_y", 0F).forGetter(BlockCollisionCondition::offsetY),
            Codec.FLOAT.optionalFieldOf("offset_z", 0F).forGetter(BlockCollisionCondition::offsetZ)
    ).apply(i, BlockCollisionCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Level level = entity.level();
        AABB bb = entity.getBoundingBox().move(offsetX, offsetY, offsetZ);
        BlockPos min = BlockPos.containing(bb.minX, bb.minY, bb.minZ);
        BlockPos max = BlockPos.containing(bb.maxX, bb.maxY, bb.maxZ);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (!state.getCollisionShape(level, pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
