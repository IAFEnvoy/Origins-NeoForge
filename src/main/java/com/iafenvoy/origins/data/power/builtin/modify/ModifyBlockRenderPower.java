package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.render.LevelRenderHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModifyBlockRenderPower extends Power {
    public static final MapCodec<ModifyBlockRenderPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBlockRenderPower::getBlockCondition),
            Block.CODEC.fieldOf("block").forGetter(ModifyBlockRenderPower::getBlock)
    ).apply(i, ModifyBlockRenderPower::new));
    private final BlockCondition blockCondition;
    private final Block block;

    public ModifyBlockRenderPower(BaseSettings settings, BlockCondition blockCondition, Block block) {
        super(settings);
        this.blockCondition = blockCondition;
        this.block = block;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        LevelRenderHelper.sendReloadPayload(holder.getEntity());
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        LevelRenderHelper.sendReloadPayload(holder.getEntity());
    }
}
