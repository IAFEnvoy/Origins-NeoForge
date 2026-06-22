package com.iafenvoy.origins.data.condition.builtin.block;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record LightLevelCondition(Optional<LightLayer> lightType, Comparison comparison) implements BlockCondition {
    public static final MapCodec<LightLevelCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraEnumCodecs.LIGHT_LAYER.optionalFieldOf("light_type").forGetter(LightLevelCondition::lightType),
            Comparison.CODEC.forGetter(LightLevelCondition::comparison)
    ).apply(i, LightLevelCondition::new));

    @Override
    public @NotNull MapCodec<? extends BlockCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull BlockPos pos) {
        int lightLevel = this.lightType
                .map(lt -> level.getBrightness(lt, pos))
                .orElseGet(() -> level.getMaxLocalRawBrightness(pos));
        return this.comparison.compare(lightLevel);
    }
}
