package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EntityAction extends Consumer<Entity> {
    Codec<EntityAction> CODEC = ActionRegistries.ENTITY_ACTION.byNameCodec().dispatch("type", EntityAction::codec, x -> x);

    @NotNull
    MapCodec<? extends EntityAction> codec();

    @Override
    void accept(@NotNull Entity source);
}
