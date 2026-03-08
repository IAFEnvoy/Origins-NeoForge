package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.data.action.builtin.entity.meta.AndAction;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface EntityAction {
    Codec<EntityAction> DISPATCH = ActionRegistries.ENTITY_ACTION.byNameCodec()
            .dispatch(Constants.TYPE_KEY, EntityAction::codec, Function.identity());

    Codec<EntityAction> CODEC = new DefaultedCodec<>(
            Codec.either(DISPATCH.listOf(), DISPATCH)
                    .xmap(
                            either -> either.map(
                                    list -> (EntityAction) new AndAction(list),
                                    single -> single
                            ),
                            action -> Either.right(action)
                    ),
            () -> NoOpAction.INSTANCE,
            ActionRegistries.ENTITY_ACTION.key().location().toString()
    );

    static MapCodec<EntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, NoOpAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityAction> codec();

    void execute(@NotNull Entity source);
}
