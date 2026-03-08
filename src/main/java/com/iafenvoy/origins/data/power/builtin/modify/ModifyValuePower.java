package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyValuePower(List<Modifier> modifiers) implements Power {

    public static final MapCodec<ModifyValuePower> CODEC =
            ListConfiguration.MODIFIER_CODEC.xmap(ModifyValuePower::new, ModifyValuePower::modifiers);

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(modifiers, baseValue);
    }
}
