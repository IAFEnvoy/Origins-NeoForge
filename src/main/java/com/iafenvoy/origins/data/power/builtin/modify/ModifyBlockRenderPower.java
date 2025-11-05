package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record ModifyBlockRenderPower(BlockCondition blockCondition,Block block) implements Power {

    public static final MapCodec<ModifyBlockRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBlockRenderPower::blockCondition),
            Block.CODEC.fieldOf("block").forGetter(ModifyBlockRenderPower::block)
    ).apply(i, ModifyBlockRenderPower::new));
    
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    public boolean test(Level world, BlockPos pos) {
        return blockCondition().test(world, pos);
    }

    @Override
    public void grant(@NotNull Entity entity) {
        // TODO
//        ApoliClient.shouldReloadWorldRenderer = true;
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        // TODO
//        ApoliClient.shouldReloadWorldRenderer = true;
    }

}
