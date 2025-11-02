package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforgespi.Environment;
import org.jetbrains.annotations.NotNull;

public record SideAction(EntityAction action, Dist side) implements EntityAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            ExtraEnumCodecs.DIST.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (Environment.get().getDist() == this.side) this.action.execute(source);
    }
}
