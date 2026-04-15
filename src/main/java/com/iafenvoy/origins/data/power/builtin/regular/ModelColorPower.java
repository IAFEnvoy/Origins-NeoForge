package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ModelColorPower extends Power {
    public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.FLOAT.optionalFieldOf("red", 1F).forGetter(ModelColorPower::getRed),
            Codec.FLOAT.optionalFieldOf("green", 1F).forGetter(ModelColorPower::getGreen),
            Codec.FLOAT.optionalFieldOf("blue", 1F).forGetter(ModelColorPower::getBlue),
            Codec.FLOAT.optionalFieldOf("alpha", 1F).forGetter(ModelColorPower::getAlpha),
            EntityCondition.optionalCodec("condition").forGetter(ModelColorPower::getCondition)
    ).apply(i, ModelColorPower::new));
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final EntityCondition condition;

    public ModelColorPower(BaseSettings settings, float red, float green, float blue, float alpha, EntityCondition condition) {
        super(settings);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.condition = condition;
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

    public float getAlpha() {
        return this.alpha;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
