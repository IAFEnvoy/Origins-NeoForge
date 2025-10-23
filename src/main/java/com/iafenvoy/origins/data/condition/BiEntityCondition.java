package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;

import java.util.function.BiPredicate;

public interface BiEntityCondition extends BiPredicate<Entity, Entity> {
    Codec<BiEntityCondition> CODEC = ConditionRegistries.BI_ENTITY_CONDITION.byNameCodec().dispatch("type", BiEntityCondition::type, ConditionType::codec);

    ConditionType<BiEntityCondition> type();

    @Override
    boolean test(Entity source, Entity target);
}
