package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AddExperienceAction(Optional<Integer> points, Optional<Integer> levels) implements EntityAction {
    public static final MapCodec<AddExperienceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("points").forGetter(AddExperienceAction::points),
            Codec.INT.optionalFieldOf("levels").forGetter(AddExperienceAction::levels)
    ).apply(i, AddExperienceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof Player player) {
            this.points.ifPresent(player::giveExperiencePoints);
            this.levels.ifPresent(player::giveExperienceLevels);
        }
    }
}
