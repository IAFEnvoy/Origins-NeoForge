package com.iafenvoy.origins.event.client;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class NightVisionStrengthEvent extends LivingEvent {
    private Optional<Float> strength = Optional.empty();

    public NightVisionStrengthEvent(LivingEntity entity) {
        super(entity);
    }

    public void setStrength(float color) {
        this.strength = Optional.of(color);
    }

    public void clearStrength() {
        this.strength = Optional.empty();
    }

    public Optional<Float> getStrength() {
        return this.strength;
    }
}
