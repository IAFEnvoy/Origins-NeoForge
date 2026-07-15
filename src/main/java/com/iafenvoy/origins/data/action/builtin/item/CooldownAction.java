package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CooldownAction(int ticks) implements ItemAction {
    public static final MapCodec<CooldownAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ticks").forGetter(CooldownAction::ticks)
    ).apply(instance, CooldownAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
        ItemStack stack = access.get();
        if (source instanceof Player player && !stack.isEmpty()) player.getCooldowns().addCooldown(stack.getItem(), this.ticks);
    }
}
