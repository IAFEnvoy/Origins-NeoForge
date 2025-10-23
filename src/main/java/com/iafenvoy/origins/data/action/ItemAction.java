package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public interface ItemAction extends BiConsumer<Level, ItemStack> {
    Codec<ItemAction> CODEC = ActionRegistries.ITEM_ACTION.byNameCodec().dispatch("type", ItemAction::type, ActionType::codec);

    ActionType<ItemAction> type();

    @Override
    void accept(Level level, ItemStack stack);
}
