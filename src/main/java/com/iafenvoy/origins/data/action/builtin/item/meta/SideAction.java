package com.iafenvoy.origins.data.action.builtin.item.meta;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforgespi.Environment;
import org.jetbrains.annotations.NotNull;

public record SideAction(ItemAction action, Dist side) implements ItemAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            ExtraEnumCodecs.DIST.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        if (Environment.get().getDist() == this.side) this.action.execute(level, source, stack);
    }
}
