package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class InvisibilityPower extends Power {
    public static final MapCodec<InvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("render_armor", true).forGetter(InvisibilityPower::isRenderArmor),
            EntityCondition.optionalCodec("condition").forGetter(InvisibilityPower::getCondition)
    ).apply(i, InvisibilityPower::new));
    private final boolean renderArmor;
    private final EntityCondition condition;

    public InvisibilityPower(BaseSettings settings, boolean renderArmor, EntityCondition condition) {
        super(settings);
        this.renderArmor = renderArmor;
        this.condition = condition;
    }

    public boolean isRenderArmor() {
        return this.renderArmor;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
