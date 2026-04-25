package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//FIXME::Duplicate with modify attribute?
@NotImplementedYet
public class ModifyFallingPower extends Power {
    public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.DOUBLE.fieldOf("velocity").forGetter(ModifyFallingPower::getVelocity),
            Codec.BOOL.optionalFieldOf("take_fall_damage", true).forGetter(ModifyFallingPower::isTakeFallDamage),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyFallingPower::getModifiers)
    ).apply(i, ModifyFallingPower::new));

    private final double velocity;
    private final boolean takeFallDamage;
    private final List<Modifier> modifiers;

    public ModifyFallingPower(BaseSettings settings, double velocity, boolean takeFallDamage, List<Modifier> modifiers) {
        super(settings);
        this.velocity = velocity;
        this.takeFallDamage = takeFallDamage;
        this.modifiers = modifiers;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public boolean isTakeFallDamage() {
        return this.takeFallDamage;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
