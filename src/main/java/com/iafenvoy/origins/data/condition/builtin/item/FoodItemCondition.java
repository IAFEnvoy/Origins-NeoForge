package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FoodItemCondition implements ItemCondition {
    public static final FoodItemCondition INSTANCE = new FoodItemCondition();
    public static final MapCodec<FoodItemCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return stack.getFoodProperties(null) != null;
    }
}
