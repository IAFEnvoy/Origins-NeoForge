package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DamageAction(int amount, boolean ignoreUnbreaking) implements ItemAction {
    public static final MapCodec<DamageAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("amount", 1).forGetter(DamageAction::amount),
            Codec.BOOL.optionalFieldOf("ignore_unbreaking", false).forGetter(DamageAction::ignoreUnbreaking)
    ).apply(i, DamageAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        if (this.ignoreUnbreaking) stack.setDamageValue(stack.getDamageValue() - this.amount);
        else if (level instanceof ServerLevel serverLevel) stack.hurtAndBreak(this.amount, serverLevel, null, item -> {
        });
    }
}
