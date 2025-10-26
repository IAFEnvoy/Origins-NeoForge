package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ItemChanceAction(ItemAction action, float chance, ItemAction failAction) implements ItemAction {
    public static final MapCodec<ItemChanceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAction.CODEC.fieldOf("action").forGetter(ItemChanceAction::action),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(ItemChanceAction::chance),
            ItemAction.optionalCodec("fail_action").forGetter(ItemChanceAction::failAction)
    ).apply(i, ItemChanceAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        if (Math.random() < this.chance) this.action.execute(level, source, stack);
        else this.failAction.execute(level, source, stack);
    }
}
