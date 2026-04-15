package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.data.action.builtin.item.meta.AndAction;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ItemAction {
    Codec<ItemAction> SINGLE_CODEC = DefaultedCodec.registryDispatch(ActionRegistries.ITEM_ACTION, ItemAction::codec, Function.identity(), () -> NoOpAction.INSTANCE);
    Codec<ItemAction> CODEC = Codec.either(SINGLE_CODEC.listOf(), SINGLE_CODEC).xmap(e -> e.map(AndAction::new, Function.identity()), Either::right);

    static MapCodec<ItemAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, NoOpAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends ItemAction> codec();

    void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack);
}
