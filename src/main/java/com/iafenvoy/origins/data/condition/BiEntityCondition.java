package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface BiEntityCondition {
    Codec<BiEntityCondition> CODEC = ConditionRegistries.BI_ENTITY_CONDITION.byNameCodec().dispatch("type", BiEntityCondition::codec, x -> x);

    static MapCodec<BiEntityCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiEntityCondition> codec();

    boolean test(@NotNull Entity source, @NotNull Entity target);
}
