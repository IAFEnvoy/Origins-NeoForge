package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface BiEntityAction {
    Codec<BiEntityAction> CODEC = ActionRegistries.BI_ENTITY_ACTION.byNameCodec().dispatch("type", BiEntityAction::codec, x -> x);

    static MapCodec<BiEntityAction> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyAction.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiEntityAction> codec();

    void execute(@NotNull Entity source, @NotNull Entity target);
}
