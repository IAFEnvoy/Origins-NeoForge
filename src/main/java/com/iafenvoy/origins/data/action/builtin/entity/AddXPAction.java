package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

public record AddXPAction(OptionalInt points, OptionalInt levels) implements EntityAction {
    public static final MapCodec<AddXPAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MiscCodecs.integer("points").forGetter(AddXPAction::points),
            MiscCodecs.integer("levels").forGetter(AddXPAction::levels)
    ).apply(i, AddXPAction::new));

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
