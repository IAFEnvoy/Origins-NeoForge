package com.iafenvoy.origins.data.condition.builtin.item.meta;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemConstantCondition(boolean value) implements ItemCondition {
    public static final MapCodec<ItemConstantCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("value").forGetter(ItemConstantCondition::value)
    ).apply(i, ItemConstantCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return this.value;
    }
}
