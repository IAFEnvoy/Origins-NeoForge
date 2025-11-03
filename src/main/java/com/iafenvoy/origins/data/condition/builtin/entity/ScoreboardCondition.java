package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ScoreboardCondition(Optional<String> name, String objective, Comparison comparison,
                                  double compareTo) implements EntityCondition {
    public static final MapCodec<ScoreboardCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("name").forGetter(ScoreboardCondition::name),
            Codec.STRING.fieldOf("objective").forGetter(ScoreboardCondition::objective),
            Comparison.CODEC.fieldOf("comparison").forGetter(ScoreboardCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(ScoreboardCondition::compareTo)
    ).apply(i, ScoreboardCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        ScoreHolder holder = ScoreHolder.forNameOnly(this.name.orElse(entity.getScoreboardName()));
        Scoreboard scoreboard = entity.level().getScoreboard();
        return Optional.ofNullable(scoreboard.getObjective(this.objective))
                .flatMap(objective -> Optional.ofNullable(scoreboard.getPlayerScoreInfo(holder, objective)))
                .map(ReadOnlyScoreInfo::value)
                .map(score -> this.comparison.compare(score, this.compareTo))
                .orElse(false);
    }
}
