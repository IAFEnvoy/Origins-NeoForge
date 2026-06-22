package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustPower extends Power {
    public static final MapCodec<ExhaustPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("interval", 20).forGetter(ExhaustPower::getInterval),
            Codec.FLOAT.fieldOf("exhaustion").forGetter(ExhaustPower::getExhaustion)
    ).apply(i, ExhaustPower::new));
    private final int interval;
    private final float exhaustion;

    public ExhaustPower(BaseSettings settings, int interval, float exhaustion) {
        super(settings);
        this.interval = interval;
        this.exhaustion = exhaustion;
    }

    public int getInterval() {
        return this.interval;
    }

    public float getExhaustion() {
        return this.exhaustion;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void activeTick(OriginDataHolder holder) {
        super.activeTick(holder);
        if (holder.getEntity() instanceof Player player)
            player.causeFoodExhaustion(this.exhaustion);
    }

    @Override
    public int tickInterval() {
        return this.interval;
    }
}
