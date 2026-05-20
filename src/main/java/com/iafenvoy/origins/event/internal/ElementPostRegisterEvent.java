package com.iafenvoy.origins.event.internal;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;

public class ElementPostRegisterEvent<E> extends Event {
    private final WritableRegistry<E> registry;
    private final ResourceKey<E> key;
    private final E element;

    public ElementPostRegisterEvent(WritableRegistry<E> registry, ResourceKey<E> key, E element) {
        this.registry = registry;
        this.key = key;
        this.element = element;
    }

    public WritableRegistry<E> getRegistry() {
        return this.registry;
    }

    public ResourceKey<? extends Registry<E>> getRegistryKey() {
        return this.registry.key();
    }

    public ResourceKey<E> getKey() {
        return this.key;
    }

    public E getElement() {
        return this.element;
    }
}
