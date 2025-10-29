package com.iafenvoy.origins.data.condition.builtin.bientity.meta;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record InvertCondition(BiEntityCondition condition) implements BiEntityCondition {
    public static final MapCodec<InvertCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.CODEC.fieldOf("condition").forGetter(InvertCondition::condition)
    ).apply(i, InvertCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return this.condition.test(target, source);
    }
}
