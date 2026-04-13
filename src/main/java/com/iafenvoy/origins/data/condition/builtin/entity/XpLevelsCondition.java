package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record XpLevelsCondition(Comparison comparison) implements EntityCondition {
    public static final MapCodec<XpLevelsCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.forGetter(XpLevelsCondition::comparison)
    ).apply(i, XpLevelsCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof Player player && this.comparison.compare(player.experienceLevel);
    }
}
