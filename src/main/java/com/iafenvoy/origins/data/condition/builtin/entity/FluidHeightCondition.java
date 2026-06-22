package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public record FluidHeightCondition(FluidType fluid, Comparison comparison) implements EntityCondition {
    public static final MapCodec<FluidHeightCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NeoForgeRegistries.FLUID_TYPES.byNameCodec().fieldOf("fluid").forGetter(FluidHeightCondition::fluid),
            Comparison.CODEC.forGetter(FluidHeightCondition::comparison)
    ).apply(i, FluidHeightCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        // 26.1版本：NeoForge基于FluidType的高度访问器已被移除；通过原版流体标签来近似替代。
        TagKey<Fluid> tag = this.fluid == NeoForgeMod.LAVA_TYPE.value() ? FluidTags.LAVA : FluidTags.WATER;
        return this.comparison.compare(entity.getFluidHeight(tag));
    }
}
