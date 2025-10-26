package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BiEntityAndAction(List<BiEntityAction> actions) implements BiEntityAction {
    public static final MapCodec<BiEntityAndAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.listOf().fieldOf("actions").forGetter(BiEntityAndAction::actions)
    ).apply(i, BiEntityAndAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        this.actions.forEach(x -> x.execute(source, target));
    }
}
