package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record EntityInTagCondition(TagKey<EntityType<?>> tag) implements EntityCondition {
    public static final MapCodec<EntityInTagCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(EntityInTagCondition::tag)
    ).apply(i, EntityInTagCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity.getType().is(this.tag);
    }
}
