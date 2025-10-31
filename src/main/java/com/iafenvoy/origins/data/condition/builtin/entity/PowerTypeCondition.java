package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record PowerTypeCondition(ResourceLocation powerType) implements EntityCondition {
    public static final MapCodec<PowerTypeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("power_type").forGetter(PowerTypeCondition::powerType)
    ).apply(i, PowerTypeCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        OriginDataHolder holder = OriginDataHolder.get(entity);
        return !holder.getPowers(this.powerType, Power.class).isEmpty();
    }
}
