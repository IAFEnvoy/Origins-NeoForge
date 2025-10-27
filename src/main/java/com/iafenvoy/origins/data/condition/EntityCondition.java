package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface EntityCondition {
    Codec<EntityCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.ENTITY_CONDITION, EntityCondition::codec, Function.identity(), () -> EmptyCondition.INSTANCE);

    static MapCodec<EntityCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityCondition> codec();

    boolean test(@NotNull Entity entity);
}
