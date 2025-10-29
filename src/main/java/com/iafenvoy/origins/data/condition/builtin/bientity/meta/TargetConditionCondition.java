package com.iafenvoy.origins.data.condition.builtin.bientity.meta;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TargetConditionCondition(EntityCondition condition) implements BiEntityCondition {
    public static final MapCodec<TargetConditionCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("condition").forGetter(TargetConditionCondition::condition)
    ).apply(i, TargetConditionCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return this.condition.test(target);
    }
}
