package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BonemealAction(boolean effect) implements BlockAction {
    public static final MapCodec<BonemealAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("effect", true).forGetter(BonemealAction::effect)
    ).apply(i, BonemealAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Optional<Direction> direction) {
        if (BoneMealItem.growCrop(ItemStack.EMPTY, level, pos)) { //使用假玩家版本（主要是因为懒）
            if (this.effect && !level.isClientSide())
                level.globalLevelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 0);
        } else if (direction.isPresent()) {
            Direction dir = direction.get();
            BlockState blockState = level.getBlockState(pos);
            boolean bl = blockState.isFaceSturdy(level, pos, dir);
            if (bl && BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, pos.relative(dir), dir) && this.effect && !level.isClientSide())
                level.globalLevelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos.relative(dir), 0);
        }
    }
}
