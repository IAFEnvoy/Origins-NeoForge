package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record SaturationLevelCondition(Comparison comparison, double compareTo) implements EntityCondition {
    public static final MapCodec<SaturationLevelCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.fieldOf("comparison").forGetter(SaturationLevelCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(SaturationLevelCondition::compareTo)
    ).apply(i, SaturationLevelCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof Player player && this.comparison.compare(player.getFoodData().getSaturationLevel(), this.compareTo);
    }
}
