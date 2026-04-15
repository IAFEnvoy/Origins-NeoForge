package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class EffectImmunityPower extends Power {
    public static final MapCodec<EffectImmunityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MOB_EFFECT.fieldOf("effect").forGetter(EffectImmunityPower::getEffect),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(EffectImmunityPower::getInverted)
    ).apply(i, EffectImmunityPower::new));
    private final List<Holder<MobEffect>> effect;
    private final boolean inverted;

    public EffectImmunityPower(BaseSettings settings, List<Holder<MobEffect>> effect, boolean inverted) {
        super(settings);
        this.effect = effect;
        this.inverted = inverted;
    }

    public List<Holder<MobEffect>> getEffect() {
        return this.effect;
    }

    public boolean getInverted() {
        return this.inverted;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean canApply(MobEffectInstance effectInstance) {
        return this.canApply(effectInstance.getEffect());
    }

    public boolean canApply(Holder<MobEffect> effect) {
        return !this.effect.contains(effect) ^ this.inverted;
    }

    @SubscribeEvent
    public static void disableEffectApply(MobEffectEvent.Applicable event) {
        for (EffectImmunityPower power : OriginDataHolder.get(event.getEntity()).getPowers(RegularPowers.EFFECT_IMMUNITY, EffectImmunityPower.class))
            if (!power.canApply(event.getEffectInstance()))
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }
}
