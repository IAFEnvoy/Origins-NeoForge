package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.NotNull;

//Max 54 Items
public class InventoryComponent extends PowerComponent {
    public static final MapCodec<InventoryComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CollectionCodecs.containerCodec(() -> new SimpleContainer(54)).fieldOf("container").forGetter(InventoryComponent::getContainer)
    ).apply(i, InventoryComponent::new));
    private final SimpleContainer container;

    public InventoryComponent(SimpleContainer container) {
        this.container = container;
        this.container.addListener(c -> this.markDirty());
    }

    public InventoryComponent(int size) {
        this(new SimpleContainer(size));
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    public SimpleContainer getContainer() {
        return this.container;
    }
}
