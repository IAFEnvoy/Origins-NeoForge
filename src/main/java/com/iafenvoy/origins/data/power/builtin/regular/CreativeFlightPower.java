package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class CreativeFlightPower extends Power {
    public static final MapCodec<CreativeFlightPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings)
    ).apply(i, CreativeFlightPower::new));

    public CreativeFlightPower(BaseSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull Entity entity) {
        if (entity instanceof Player player) player.getAbilities().mayfly = true;
    }

    @Override
    public void revoke(@NotNull Entity entity) {
        if (entity instanceof Player player) player.getAbilities().mayfly = false;
    }
}
