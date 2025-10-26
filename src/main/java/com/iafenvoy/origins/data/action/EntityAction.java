package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityAction {
    Codec<EntityAction> CODEC = ActionRegistries.ENTITY_ACTION.byNameCodec().dispatch("type", EntityAction::codec, x -> x);

    static MapCodec<EntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityAction> codec();

    void execute(@NotNull Entity source);
}
