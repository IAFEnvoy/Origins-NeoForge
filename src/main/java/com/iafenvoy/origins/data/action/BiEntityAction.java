package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.data.action.builtin.bientity.meta.AndAction;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BiEntityAction {
    Codec<BiEntityAction> SINGLE_CODEC = DefaultedCodec.registryDispatch(ActionRegistries.BI_ENTITY_ACTION, BiEntityAction::codec, Function.identity(), () -> NoOpAction.INSTANCE);
    Codec<BiEntityAction> CODEC = Codec.either(SINGLE_CODEC.listOf(), SINGLE_CODEC).xmap(e -> e.map(AndAction::new, Function.identity()), Either::right);

    static MapCodec<BiEntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, NoOpAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiEntityAction> codec();

    void execute(@NotNull Entity source, @NotNull Entity target);
}
