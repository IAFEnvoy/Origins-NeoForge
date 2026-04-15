package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyValuePower(List<Modifier> modifiers) implements Power {

    public static final MapCodec<ModifyValuePower> CODEC =
            CombinedCodecs.MODIFIER.fieldOf("modifier").xmap(ModifyValuePower::new, ModifyValuePower::modifiers);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
