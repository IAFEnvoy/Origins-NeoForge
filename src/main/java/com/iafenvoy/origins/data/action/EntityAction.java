package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.data.action.builtin.entity.meta.AndAction;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface EntityAction {
    Codec<EntityAction> SINGLE_CODEC = DefaultedCodec.registryDispatch(ActionRegistries.ENTITY_ACTION, EntityAction::codec, Function.identity(), () -> NoOpAction.INSTANCE);
    Codec<EntityAction> CODEC = Codec.either(SINGLE_CODEC.listOf(), SINGLE_CODEC).xmap(e -> e.map(AndAction::new, Function.identity()), Either::right);

    static MapCodec<EntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, NoOpAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityAction> codec();

    void execute(@NotNull Entity source);
}
