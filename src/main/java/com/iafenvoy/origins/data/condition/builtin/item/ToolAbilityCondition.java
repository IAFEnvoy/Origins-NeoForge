package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public record ToolAbilityCondition(ItemAbility ability) implements ItemCondition {
    public static final MapCodec<ToolAbilityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAbility.CODEC.fieldOf("ability").forGetter(ToolAbilityCondition::ability)
    ).apply(i, ToolAbilityCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return stack.canPerformAction(this.ability);
    }
}
