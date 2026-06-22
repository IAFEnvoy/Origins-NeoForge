package com.iafenvoy.origins.data._common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 一个物品堆叠定义，可以在数据驱动注册表加载期间解码，
 * 此时 Minecraft 尚未将默认组件附加到物品上。
 */
public record ItemStackReference(Holder<Item> item, int count, DataComponentPatch components) {
    public static final Codec<ItemStackReference> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("id").forGetter(ItemStackReference::item),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStackReference::count),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStackReference::components)
    ).apply(i, ItemStackReference::new));

    public static ItemStackReference of(ItemStack stack) {
        return new ItemStackReference(stack.typeHolder(), stack.getCount(), stack.getComponentsPatch());
    }

    public ItemStack create() {
        return new ItemStack(this.item, this.count, this.components);
    }
}
