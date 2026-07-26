package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;

/** Checks whether an entity belongs to any team or to a named team. */
public record TeamCondition(Optional<String> team) implements EntityCondition {
    public static final MapCodec<TeamCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("team").forGetter(TeamCondition::team)
    ).apply(instance, TeamCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        PlayerTeam currentTeam = entity.getTeam();
        return currentTeam != null && this.team.map(currentTeam.getName()::equals).orElse(true);
    }
}
