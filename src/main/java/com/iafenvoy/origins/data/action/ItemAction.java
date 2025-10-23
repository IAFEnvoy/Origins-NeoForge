package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface ItemAction extends BiConsumer<Level, ItemStack> {
    Codec<ItemAction> CODEC = ActionRegistries.ITEM_ACTION.byNameCodec().dispatch("type", ItemAction::codec, x -> x);

    MapCodec<? extends ItemAction> codec();

    @Override
    void accept(Level level, ItemStack stack);
}
