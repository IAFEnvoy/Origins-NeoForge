package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyStatusEffectPower(List<Holder<MobEffect>> effects) implements Power {

    public static final MapCodec<ModifyStatusEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MobEffect.CODEC.listOf().fieldOf("status_effects").forGetter(ModifyStatusEffectPower::effects)
    ).apply(i, ModifyStatusEffectPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyStatusEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.mapCodec(SerializableDataTypes.STATUS_EFFECT, "status_effect", "status_effects").forGetter(ModifyStatusEffectConfiguration::effects),
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyStatusEffectConfiguration::modifiers)
//    ).apply(instance, ModifyStatusEffectConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    public static boolean doesApply(ModifyStatusEffectPower power, MobEffect effect) {
        return power.effects().isEmpty() || power.effects().contains(Holder.direct(effect));
    }
}
