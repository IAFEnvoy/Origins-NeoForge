package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.EntitySetAttachment;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record SetSizeCondition(ResourceLocation set, Comparison comparison,
                               double compareTo) implements EntityCondition {
    public static final MapCodec<SetSizeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("set").forGetter(SetSizeCondition::set),
            Comparison.CODEC.fieldOf("comparison").forGetter(SetSizeCondition::comparison),
            Codec.DOUBLE.fieldOf("compare_to").forGetter(SetSizeCondition::compareTo)
    ).apply(i, SetSizeCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.comparison.compare(EntitySetAttachment.get(entity).getSize(this.set), this.compareTo);
    }
}
