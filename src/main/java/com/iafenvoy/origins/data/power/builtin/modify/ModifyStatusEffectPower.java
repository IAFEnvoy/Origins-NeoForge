package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyStatusEffectPower extends Power {
    public static final MapCodec<ModifyStatusEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            MobEffect.CODEC.listOf().optionalFieldOf("status_effects", List.of()).forGetter(ModifyStatusEffectPower::getEffects),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyStatusEffectPower::getModifiers)
    ).apply(i, ModifyStatusEffectPower::new));
    private final List<Holder<MobEffect>> effects;
    private final List<Modifier> modifiers;

    public ModifyStatusEffectPower(BaseSettings settings, List<Holder<MobEffect>> effects, List<Modifier> modifiers) {
        super(settings);
        this.effects = effects;
        this.modifiers = modifiers;
    }

    public List<Holder<MobEffect>> getEffects() {
        return this.effects;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean doesApply(MobEffect effect) {
        return this.effects.isEmpty() || this.effects.contains(Holder.direct(effect));
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
