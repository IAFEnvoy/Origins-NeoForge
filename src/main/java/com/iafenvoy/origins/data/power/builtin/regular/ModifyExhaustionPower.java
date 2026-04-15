package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyExhaustionPower extends Power {
    public static final MapCodec<ModifyExhaustionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyExhaustionPower::getModifiers),
            EntityCondition.optionalCodec("condition").forGetter(ModifyExhaustionPower::getCondition)
    ).apply(i, ModifyExhaustionPower::new));
    private final List<Modifier> modifiers;
    private final EntityCondition condition;

    public ModifyExhaustionPower(BaseSettings settings, List<Modifier> modifiers, EntityCondition condition) {
        super(settings);
        this.modifiers = modifiers;
        this.condition = condition;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
