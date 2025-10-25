package com.iafenvoy.origins.event.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ElytraTextureEvent extends Event {
    private final LivingEntity entity;
    private Optional<ResourceLocation> texture = Optional.empty();

    public ElytraTextureEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public void setTexture(@NotNull ResourceLocation texture) {
        this.texture = Optional.of(texture);
    }

    public void clearTexture() {
        this.texture = Optional.empty();
    }

    public Optional<ResourceLocation> getTexture() {
        return this.texture;
    }
}
