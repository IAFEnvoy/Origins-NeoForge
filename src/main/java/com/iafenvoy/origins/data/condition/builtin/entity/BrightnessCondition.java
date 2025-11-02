package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.MiscUtil;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record BrightnessCondition(Comparison comparison, double compareTo) implements EntityCondition {
    public static final MapCodec<BrightnessCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(BrightnessCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(BrightnessCondition::compareTo)
    ).apply(i, BrightnessCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        //FIXME::Is this right?
        Level world = entity.level();
        return this.comparison.compare(world.getLightLevelDependentMagicValue(BlockPos.containing(MiscUtil.getPoseDependentEyePos(entity))), this.compareTo)
                || this.comparison.compare(world.getLightLevelDependentMagicValue(entity.blockPosition()), this.compareTo);
    }
}
