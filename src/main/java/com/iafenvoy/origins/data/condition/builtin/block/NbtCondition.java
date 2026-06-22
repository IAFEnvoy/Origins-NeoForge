package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record NbtCondition(CompoundTag nbt) implements BlockCondition {
    public static final MapCodec<NbtCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CompoundTag.CODEC.fieldOf("nbt").forGetter(NbtCondition::nbt)
    ).apply(i, NbtCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        CompoundTag nbt = new CompoundTag();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) nbt = blockEntity.getUpdateTag(level.registryAccess());
        return NbtUtils.compareNbt(this.nbt, nbt, true);
    }
}
