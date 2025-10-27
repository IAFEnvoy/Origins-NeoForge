package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ItemAction {
    Codec<ItemAction> CODEC = DefaultedCodec.registryDispatch(ActionRegistries.ITEM_ACTION, ItemAction::codec, Function.identity(), () -> EmptyAction.INSTANCE);

    static MapCodec<ItemAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends ItemAction> codec();

    void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack);
}
