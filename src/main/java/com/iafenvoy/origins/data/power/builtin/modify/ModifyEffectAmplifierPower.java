package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyEffectAmplifierPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyEffectAmplifierPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            MobEffect.CODEC.listOf().optionalFieldOf("effect", List.of()).forGetter(ModifyEffectAmplifierPower::getEffect),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyEffectAmplifierPower::getModifier)
    ).apply(i, ModifyEffectAmplifierPower::new));
    private final List<Holder<MobEffect>> effect;
    private final List<Modifier> modifier;

    public ModifyEffectAmplifierPower(BaseSettings settings, List<Holder<MobEffect>> effect, List<Modifier> modifier) {
        super(settings);
        this.effect = effect;
        this.modifier = modifier;
    }

    public List<Holder<MobEffect>> getEffect() {
        return this.effect;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean doesApply(Holder<MobEffect> effect) {
        return this.effect.isEmpty() || this.effect.contains(effect);
    }
}
