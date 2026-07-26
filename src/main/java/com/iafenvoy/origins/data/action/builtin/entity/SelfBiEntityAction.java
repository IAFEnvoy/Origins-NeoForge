package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public record SelfBiEntityAction(BiEntityAction action) implements EntityAction {
    public static final MapCodec<SelfBiEntityAction> CODEC = BiEntityAction.CODEC.fieldOf("action")
            .xmap(SelfBiEntityAction::new, SelfBiEntityAction::action);

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(net.minecraft.world.entity.@NotNull Entity entity) {
        this.action.execute(entity, entity);
    }
}
