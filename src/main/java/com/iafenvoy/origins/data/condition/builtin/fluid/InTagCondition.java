package com.iafenvoy.origins.data.condition.builtin.fluid;

import com.iafenvoy.origins.data.condition.FluidCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public record InTagCondition(TagKey<Fluid> tag) implements FluidCondition {
    public static final MapCodec<InTagCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(InTagCondition::tag)
    ).apply(i, InTagCondition::new));

    @Override
    public @NotNull MapCodec<? extends FluidCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull FluidState state) {
        return state.is(this.tag);
    }
}
