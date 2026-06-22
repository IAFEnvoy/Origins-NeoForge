package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyInsomniaTicksPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyInsomniaTicksPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Modifier.CODEC.listOf().fieldOf("modifier").forGetter(ModifyInsomniaTicksPower::getModifier)
    ).apply(i, ModifyInsomniaTicksPower::new));
    private final List<Modifier> modifier;

    public ModifyInsomniaTicksPower(BaseSettings settings, List<Modifier> modifier) {
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
