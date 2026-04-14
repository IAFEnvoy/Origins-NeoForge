package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ResourceCondition(ResourceLocation resource, Comparison comparison) implements EntityCondition {
    public static final MapCodec<ResourceCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(ResourceCondition::resource),
            Comparison.CODEC.forGetter(ResourceCondition::comparison)
    ).apply(i, ResourceCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.comparison.compare(OriginDataHolder.get(entity).getResource(this.resource));
    }
}
