package com.iafenvoy.origins.data.condition.builtin.bientity.meta;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EitherCondition(EntityCondition condition) implements BiEntityCondition {
    public static final MapCodec<EitherCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("condition").forGetter(EitherCondition::condition)
    ).apply(i, EitherCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return this.condition.test(source) || this.condition.test(target);
    }
}
