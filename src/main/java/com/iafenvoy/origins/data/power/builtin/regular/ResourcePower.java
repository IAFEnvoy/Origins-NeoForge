package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.render.HudRender;
import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public record ResourcePower(int min, int max, Optional<HudRender> hudRender, OptionalInt startValue,
                            EntityAction minAction, EntityAction maxAction) implements Power {
    public static final MapCodec<ResourcePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("min").forGetter(ResourcePower::min),
            Codec.INT.fieldOf("max").forGetter(ResourcePower::max),
            HudRender.CODEC.optionalFieldOf("hud_render").forGetter(ResourcePower::hudRender),
            OptionalCodecs.integer("start_value").forGetter(ResourcePower::startValue),
            EntityAction.optionalCodec("min_action").forGetter(ResourcePower::minAction),
            EntityAction.optionalCodec("max_action").forGetter(ResourcePower::maxAction)
    ).apply(i, ResourcePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new ResourceComponent(this.startValue.orElse(this.min)));
    }
}
