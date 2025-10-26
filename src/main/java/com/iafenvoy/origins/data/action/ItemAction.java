package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ItemAction {
    Codec<ItemAction> CODEC = ActionRegistries.ITEM_ACTION.byNameCodec().dispatch("type", ItemAction::codec, x -> x);

    static MapCodec<ItemAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends ItemAction> codec();

    void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack);
}
