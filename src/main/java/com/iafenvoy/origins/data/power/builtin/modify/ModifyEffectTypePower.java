package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyEffectTypePower extends Power {
    public static final MapCodec<ModifyEffectTypePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MOB_EFFECT.optionalFieldOf("effect", List.of()).forGetter(ModifyEffectTypePower::getEffect),
            MobEffect.CODEC.fieldOf("new_effect").forGetter(ModifyEffectTypePower::getNewEffect)
    ).apply(i, ModifyEffectTypePower::new));
    private final List<Holder<MobEffect>> effect;
    private final Holder<MobEffect> newEffect;

    protected ModifyEffectTypePower(BaseSettings settings, List<Holder<MobEffect>> effect, Holder<MobEffect> newEffect) {
        super(settings);
        this.effect = effect;
        this.newEffect = newEffect;
    }

    public List<Holder<MobEffect>> getEffect() {
        return this.effect;
    }

    public Holder<MobEffect> getNewEffect() {
        return this.newEffect;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
