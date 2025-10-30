package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforgespi.Environment;
import org.jetbrains.annotations.NotNull;

public record BiEntitySideAction(BiEntityAction action, Dist side) implements BiEntityAction {
    public static final MapCodec<BiEntitySideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(BiEntitySideAction::action),
            ExtraEnumCodecs.DIST.fieldOf("side").forGetter(BiEntitySideAction::side)
    ).apply(i, BiEntitySideAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        if (Environment.get().getDist() == this.side) this.action.execute(source, target);
    }
}
