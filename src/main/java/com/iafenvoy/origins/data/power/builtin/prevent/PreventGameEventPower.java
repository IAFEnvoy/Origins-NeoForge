package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
public class PreventGameEventPower extends Power {
    public static final MapCodec<PreventGameEventPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceLocation.CODEC.optionalFieldOf("event").forGetter(PreventGameEventPower::getEvent),
            Codec.STRING.optionalFieldOf("tag", "").forGetter(PreventGameEventPower::getEventTag),
            EntityCondition.optionalCodec("condition").forGetter(PreventGameEventPower::getCondition)
    ).apply(i, PreventGameEventPower::new));
    private final Optional<ResourceLocation> event;
    private final String eventTag;
    private final EntityCondition condition;

    public PreventGameEventPower(BaseSettings settings, Optional<ResourceLocation> event, String eventTag, EntityCondition condition) {
        super(settings);
        this.event = event;
        this.eventTag = eventTag;
        this.condition = condition;
    }

    public Optional<ResourceLocation> getEvent() {
        return this.event;
    }

    public String getEventTag() {
        return this.eventTag;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
