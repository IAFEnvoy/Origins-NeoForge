package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyStatusEffectPower(List<Holder<MobEffect>> effects, List<Modifier> modifiers) implements Power {

    public static final MapCodec<ModifyStatusEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MobEffect.CODEC.listOf().optionalFieldOf("status_effects", List.of()).forGetter(ModifyStatusEffectPower::effects),
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyStatusEffectPower::modifiers)
    ).apply(i, ModifyStatusEffectPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean doesApply(MobEffect effect) {
        return effects.isEmpty() || effects.contains(Holder.direct(effect));
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(modifiers, baseValue);
    }
}
