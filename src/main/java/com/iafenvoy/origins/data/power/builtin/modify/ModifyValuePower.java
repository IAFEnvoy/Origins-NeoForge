package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyValuePower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyValuePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyValuePower::getModifier)
    ).apply(i, ModifyValuePower::new));
    private final List<Modifier> modifier;

    public ModifyValuePower(BaseSettings settings, List<Modifier> modifier) {
        super(settings);
        this.modifier = modifier;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
