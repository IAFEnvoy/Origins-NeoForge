package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.common.CooldownSettings;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CooldownPower extends HasCooldownPower {
    public static final MapCodec<CooldownPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(CooldownPower::getCooldown)
    ).apply(i, CooldownPower::new));

    public CooldownPower(BaseSettings settings, CooldownSettings cooldown) {
        super(settings, cooldown);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(OriginDataHolder holder) {
        return holder.getComponentFor(this, CooldownComponent.class).map(CooldownComponent::canUse).orElse(true);
    }
}
