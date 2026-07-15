package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class OnCooldownCondition implements ItemCondition {
    public static final OnCooldownCondition INSTANCE = new OnCooldownCondition();
    public static final MapCodec<OnCooldownCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    private OnCooldownCondition() {
    }

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return EntityLinkedItemStack.getEntity(stack) instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem());
    }
}
