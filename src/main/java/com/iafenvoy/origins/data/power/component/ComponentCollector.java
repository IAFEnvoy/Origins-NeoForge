package com.iafenvoy.origins.data.power.component;

import com.google.common.collect.ImmutableMap;

public class ComponentCollector {
    private final ImmutableMap.Builder<Class<? extends PowerComponent>, PowerComponent> builder;

    protected ComponentCollector() {
        this.builder = ImmutableMap.builder();
    }

    public static ComponentCollector create() {
        return new ComponentCollector();
    }

    public void add(PowerComponent component) {
        this.builder.put(component.getClass(), component);
    }

    public ImmutableMap<Class<? extends PowerComponent>, PowerComponent> build() {
        return this.builder.buildOrThrow();
    }
}
