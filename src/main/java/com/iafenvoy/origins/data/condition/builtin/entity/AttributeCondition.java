package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

public record AttributeCondition(Holder<Attribute> attribute, Comparison comparison,
                                 double compareTo) implements EntityCondition {
    public static final MapCodec<AttributeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeCondition::attribute),
            Comparison.CODEC.fieldOf("comparison").forGetter(AttributeCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(AttributeCondition::compareTo)
    ).apply(i, AttributeCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof LivingEntity living && this.comparison.compare(living.getAttributeValue(this.attribute), this.compareTo);
    }
}
