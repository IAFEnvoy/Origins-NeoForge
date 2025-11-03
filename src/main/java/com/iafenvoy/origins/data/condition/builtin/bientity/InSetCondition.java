package com.iafenvoy.origins.data.condition.builtin.bientity;

import com.iafenvoy.origins.attachment.EntitySetAttachment;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record InSetCondition(ResourceLocation set) implements BiEntityCondition {
    public static final MapCodec<InSetCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("set").forGetter(InSetCondition::set)
    ).apply(i, InSetCondition::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity source, @NotNull Entity target) {
        return EntitySetAttachment.get(source).containEntity(this.set, target);
    }
}
