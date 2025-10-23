package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface BiEntityCondition extends BiPredicate<Entity, Entity> {
    Codec<BiEntityCondition> CODEC = ConditionRegistries.BI_ENTITY_CONDITION.byNameCodec().dispatch("type", BiEntityCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends BiEntityCondition> codec();

    @Override
    boolean test(@NotNull Entity source, @NotNull Entity target);
}
