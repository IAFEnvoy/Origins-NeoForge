package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
public class OverlayPower extends Power {
    public static final MapCodec<OverlayPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.STRING.optionalFieldOf("sprite").forGetter(OverlayPower::getSprite),
            Codec.FLOAT.optionalFieldOf("red", 1F).forGetter(OverlayPower::getRed),
            Codec.FLOAT.optionalFieldOf("green", 1F).forGetter(OverlayPower::getGreen),
            Codec.FLOAT.optionalFieldOf("blue", 1F).forGetter(OverlayPower::getBlue),
            Codec.FLOAT.optionalFieldOf("strength", 1F).forGetter(OverlayPower::getStrength),
            Codec.STRING.optionalFieldOf("draw_mode", "texture").forGetter(OverlayPower::getDrawMode),
            Codec.STRING.optionalFieldOf("draw_phase", "below_hud").forGetter(OverlayPower::getDrawPhase),
            Codec.BOOL.optionalFieldOf("visible_in_third_person", false).forGetter(OverlayPower::isVisibleInThirdPerson),
            Codec.BOOL.optionalFieldOf("hide_with_hud", true).forGetter(OverlayPower::isHideWithHud),
            EntityCondition.optionalCodec("condition").forGetter(OverlayPower::getCondition)
    ).apply(i, OverlayPower::new));
    private final Optional<String> sprite;
    private final float red;
    private final float green;
    private final float blue;
    private final float strength;
    private final String drawMode;
    private final String drawPhase;
    private final boolean visibleInThirdPerson;
    private final boolean hideWithHud;
    private final EntityCondition condition;

    public OverlayPower(BaseSettings settings, Optional<String> sprite, float red, float green, float blue, float strength, String drawMode, String drawPhase, boolean visibleInThirdPerson, boolean hideWithHud, EntityCondition condition) {
        super(settings);
        this.sprite = sprite;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.strength = strength;
        this.drawMode = drawMode;
        this.drawPhase = drawPhase;
        this.visibleInThirdPerson = visibleInThirdPerson;
        this.hideWithHud = hideWithHud;
        this.condition = condition;
    }

    public Optional<String> getSprite() {
        return this.sprite;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getStrength() {
        return this.strength;
    }

    public String getDrawMode() {
        return this.drawMode;
    }

    public String getDrawPhase() {
        return this.drawPhase;
    }

    public boolean isVisibleInThirdPerson() {
        return this.visibleInThirdPerson;
    }

    public boolean isHideWithHud() {
        return this.hideWithHud;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
