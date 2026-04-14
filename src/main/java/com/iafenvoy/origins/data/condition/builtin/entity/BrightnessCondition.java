package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BrightnessCondition(Comparison comparison) implements EntityCondition {
    public static final MapCodec<BrightnessCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.forGetter(BrightnessCondition::comparison)
    ).apply(i, BrightnessCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Level world = entity.level();
        BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
        boolean chunkLoaded = world.getChunkSource().hasChunk(
                SectionPos.blockToSectionCoord(blockPos.getX()),
                SectionPos.blockToSectionCoord(blockPos.getZ()));
        float brightness;
        if (chunkLoaded) {
            float f = (float) world.getMaxLocalRawBrightness(blockPos) / 15.0F;
            float f1 = f / (4.0F - 3.0F * f);
            brightness = Mth.lerp(world.dimensionType().ambientLight(), f1, 1.0F);
        } else {
            brightness = 0.0f;
        }
        return this.comparison.compare(brightness);
    }
}
