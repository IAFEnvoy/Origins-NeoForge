package com.iafenvoy.origins.data.action;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BiEntityAction {
    Codec<BiEntityAction> CODEC = DefaultedCodec.registryDispatch(ActionRegistries.BI_ENTITY_ACTION, BiEntityAction::codec, Function.identity(), () -> EmptyAction.INSTANCE);

    static MapCodec<BiEntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiEntityAction> codec();

    void execute(@NotNull Entity source, @NotNull Entity target);
}
