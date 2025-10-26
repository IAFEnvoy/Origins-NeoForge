package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record FeedAction(int food, float saturation) implements EntityAction {
    public static final MapCodec<FeedAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("food").forGetter(FeedAction::food),
            Codec.FLOAT.fieldOf("saturation").forGetter(FeedAction::saturation)
    ).apply(i, FeedAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof Player player) player.getFoodData().eat(this.food, this.saturation);
    }
}
