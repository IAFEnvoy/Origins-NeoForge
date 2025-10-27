package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface EntityAction {
    Codec<EntityAction> CODEC = DefaultedCodec.registryDispatch(ActionRegistries.ENTITY_ACTION, EntityAction::codec, Function.identity(), () -> EmptyAction.INSTANCE);

    static MapCodec<EntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityAction> codec();

    void execute(@NotNull Entity source);
}
