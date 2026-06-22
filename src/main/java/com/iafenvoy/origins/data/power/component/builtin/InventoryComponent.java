package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//最多 54 个物品
public class InventoryComponent extends PowerComponent {
    public static final MapCodec<InventoryComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CollectionCodecs.containerCodec(() -> new SimpleContainer(54)).fieldOf("container").forGetter(InventoryComponent::getContainer)
    ).apply(i, InventoryComponent::new));
    private final SimpleContainer container;
    private List<ItemStack> snapshot;

    public InventoryComponent(SimpleContainer container) {
        this.container = container;
        this.snapshot = this.copyContents();
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

    @Override
    public void tick(OriginDataHolder holder, PowerHolder parent) {
        List<ItemStack> current = this.copyContents();
        if (current.size() != this.snapshot.size()) {
            this.markDirty();
        } else {
            for (int i = 0; i < current.size(); i++) {
                if (!ItemStack.matches(current.get(i), this.snapshot.get(i))) {
                    this.markDirty();
                    break;
                }
            }
        }
        this.snapshot = current;
    }

    private List<ItemStack> copyContents() {
        List<ItemStack> result = new ArrayList<>(this.container.getContainerSize());
        for (int i = 0; i < this.container.getContainerSize(); i++) result.add(this.container.getItem(i).copy());
        return result;
    }
}
