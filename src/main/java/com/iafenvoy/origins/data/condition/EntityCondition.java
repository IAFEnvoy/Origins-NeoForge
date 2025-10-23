package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface EntityCondition extends Predicate<Entity> {
    Codec<EntityCondition> CODEC = ConditionRegistries.ENTITY_CONDITION.byNameCodec().dispatch("type", EntityCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends EntityCondition> codec();

    @Override
    boolean test(@NotNull Entity entity);
}
