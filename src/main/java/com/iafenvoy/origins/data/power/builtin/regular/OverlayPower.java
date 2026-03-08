package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record OverlayPower(Optional<String> sprite, float red, float green, float blue,
                           float strength, String drawMode, String drawPhase,
                           boolean visibleInThirdPerson, boolean hideWithHud,
                           EntityCondition condition) implements Power {
    public static final MapCodec<OverlayPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.optionalFieldOf("sprite").forGetter(OverlayPower::sprite),
            Codec.FLOAT.optionalFieldOf("red", 1F).forGetter(OverlayPower::red),
            Codec.FLOAT.optionalFieldOf("green", 1F).forGetter(OverlayPower::green),
            Codec.FLOAT.optionalFieldOf("blue", 1F).forGetter(OverlayPower::blue),
            Codec.FLOAT.optionalFieldOf("strength", 1F).forGetter(OverlayPower::strength),
            Codec.STRING.optionalFieldOf("draw_mode", "texture").forGetter(OverlayPower::drawMode),
            Codec.STRING.optionalFieldOf("draw_phase", "below_hud").forGetter(OverlayPower::drawPhase),
            Codec.BOOL.optionalFieldOf("visible_in_third_person", false).forGetter(OverlayPower::visibleInThirdPerson),
            Codec.BOOL.optionalFieldOf("hide_with_hud", true).forGetter(OverlayPower::hideWithHud),
            EntityCondition.optionalCodec("condition").forGetter(OverlayPower::condition)
    ).apply(i, OverlayPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
