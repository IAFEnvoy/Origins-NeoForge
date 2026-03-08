package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record LaunchPower(int cooldown, float speed,
                          EntityCondition condition) implements Power {
    public static final MapCodec<LaunchPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(LaunchPower::cooldown),
            Codec.FLOAT.optionalFieldOf("speed", 1F).forGetter(LaunchPower::speed),
            EntityCondition.optionalCodec("condition").forGetter(LaunchPower::condition)
    ).apply(i, LaunchPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
