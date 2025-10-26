package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
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
public record EffectImmunityPower(List<Holder<MobEffect>> effect, boolean inverted) implements Power {
    public static final MapCodec<EffectImmunityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.MOB_EFFECT.fieldOf("effect").forGetter(EffectImmunityPower::effect),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(EffectImmunityPower::inverted)
    ).apply(i, EffectImmunityPower::new));

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
        for (Power power : EntityOriginAttachment.get(event.getEntity()).getPowers(RegularPowers.EFFECT_IMMUNITY))
            if (power instanceof EffectImmunityPower effectImmunity && !effectImmunity.canApply(event.getEffectInstance()))
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }
}
