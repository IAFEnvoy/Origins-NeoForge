package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityCondition {
    Codec<EntityCondition> CODEC = ConditionRegistries.ENTITY_CONDITION.byNameCodec().dispatch("type", EntityCondition::codec, x -> x);

    static MapCodec<EntityCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends EntityCondition> codec();

    boolean test(@NotNull Entity entity);
}
