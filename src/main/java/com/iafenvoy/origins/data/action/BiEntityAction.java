package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface BiEntityAction extends BiConsumer<Entity, Entity> {
    Codec<BiEntityAction> CODEC = ActionRegistries.BI_ENTITY_ACTION.byNameCodec().dispatch("type", BiEntityAction::codec, x -> x);

    @NotNull
    MapCodec<? extends BiEntityAction> codec();

    @Override
    void accept(@NotNull Entity source, @NotNull Entity target);
}
