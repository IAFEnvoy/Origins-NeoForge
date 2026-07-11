package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data._common.StatReference;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record StatCondition(StatReference stat, Comparison comparison) implements EntityCondition {
    public static final MapCodec<StatCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            StatReference.CODEC.fieldOf("stat").forGetter(StatCondition::stat),
            Comparison.CODEC.forGetter(StatCondition::comparison)
    ).apply(i, StatCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return false;
        Stat<?> resolved = this.stat.resolve();
        if (resolved == null) return false;
        return this.comparison.compare(player.getStats().getValue(resolved));
    }
}
