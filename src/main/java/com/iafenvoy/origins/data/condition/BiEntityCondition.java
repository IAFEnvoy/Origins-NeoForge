package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BiEntityCondition {
    Codec<BiEntityCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.BI_ENTITY_CONDITION, BiEntityCondition::codec, Function.identity(), () -> EmptyCondition.INSTANCE);

    static MapCodec<BiEntityCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends BiEntityCondition> codec();

    boolean test(@NotNull Entity source, @NotNull Entity target);
}
