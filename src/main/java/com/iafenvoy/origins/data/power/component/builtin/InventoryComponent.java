package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.NotNull;

//Max 54 Items
public record InventoryComponent(SimpleContainer container) implements PowerComponent {
    public static final MapCodec<InventoryComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CollectionCodecs.containerCodec(() -> new SimpleContainer(54)).fieldOf("container").forGetter(ic -> ic.container)
    ).apply(i, InventoryComponent::new));

    public InventoryComponent(int size) {
        this(new SimpleContainer(size));
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }
}
