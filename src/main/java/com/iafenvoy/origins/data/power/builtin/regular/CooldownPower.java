package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CooldownPower extends Power {
    public static final MapCodec<CooldownPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(CooldownPower::getCooldown)
    ).apply(i, CooldownPower::new));
    private final int cooldown;

    public CooldownPower(BaseSettings settings, int cooldown) {
        super(settings);
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new CooldownComponent(this.cooldown));
    }

    @Override
    public boolean isActive(OriginDataHolder holder) {
        return holder.getComponentFor(this, CooldownComponent.class).map(CooldownComponent::canUse).orElse(true);
    }
}
