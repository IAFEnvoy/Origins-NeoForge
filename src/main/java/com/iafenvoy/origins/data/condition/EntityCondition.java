package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public interface EntityCondition extends Predicate<Entity> {
    Codec<EntityCondition> CODEC = ConditionRegistries.ENTITY_CONDITION.byNameCodec().dispatch("type", EntityCondition::type, ConditionType::codec);

    ConditionType<EntityCondition> type();

    @Override
    boolean test(Entity entity);
}
