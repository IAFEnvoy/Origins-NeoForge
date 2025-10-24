package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public interface ItemAction extends TriConsumer<Level, Entity, ItemStack> {
    Codec<ItemAction> CODEC = ActionRegistries.ITEM_ACTION.byNameCodec().dispatch("type", ItemAction::codec, x -> x);

    @NotNull
    MapCodec<? extends ItemAction> codec();

    @Override
    void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack);
}
