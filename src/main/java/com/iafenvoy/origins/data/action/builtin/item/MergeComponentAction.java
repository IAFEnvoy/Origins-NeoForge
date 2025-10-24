package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record MergeComponentAction(DataComponentPatch components) implements ItemAction {
    public static final MapCodec<MergeComponentAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DataComponentPatch.CODEC.fieldOf("components").forGetter(MergeComponentAction::components)
    ).apply(i, MergeComponentAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        stack.applyComponents(this.components);
    }
}
