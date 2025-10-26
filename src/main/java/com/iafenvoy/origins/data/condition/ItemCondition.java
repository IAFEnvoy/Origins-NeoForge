package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ItemCondition {
    Codec<ItemCondition> CODEC = ConditionRegistries.ITEM_CONDITION.byNameCodec().dispatch("type", ItemCondition::codec, x -> x);

    static MapCodec<ItemCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends ItemCondition> codec();

    boolean test(@NotNull Level level, @NotNull ItemStack stack);
}
