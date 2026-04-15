package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventSleepPower extends Power {
    public static final MapCodec<PreventSleepPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(preventSleepPower -> preventSleepPower.getBlockCondition()),
            ComponentSerialization.CODEC.optionalFieldOf("message").forGetter(preventSleepPower -> preventSleepPower.getMessage()),
            EntityCondition.optionalCodec("condition").forGetter(preventSleepPower -> preventSleepPower.getCondition())
    ).apply(i, PreventSleepPower::new));
    private final BlockCondition blockCondition;
    private final Optional<Component> message;
    private final EntityCondition condition;

    public PreventSleepPower(BaseSettings settings, BlockCondition blockCondition, Optional<Component> message, EntityCondition condition) {
        super(settings);
        this.blockCondition = blockCondition;
        this.message = message;
        this.condition = condition;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public Optional<Component> getMessage() {
        return this.message;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
