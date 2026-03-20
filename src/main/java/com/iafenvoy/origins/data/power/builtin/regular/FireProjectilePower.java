package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record FireProjectilePower(int cooldown, Optional<EntityType<?>> entityType,
                                  float speed, float divergence,
                                  Optional<ResourceLocation> sound,
                                  EntityCondition condition) implements Power {
    public static final MapCodec<FireProjectilePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(FireProjectilePower::cooldown),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("entity_type").forGetter(FireProjectilePower::entityType),
            Codec.FLOAT.optionalFieldOf("speed", 1.5F).forGetter(FireProjectilePower::speed),
            Codec.FLOAT.optionalFieldOf("divergence", 1F).forGetter(FireProjectilePower::divergence),
            ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(FireProjectilePower::sound),
            EntityCondition.optionalCodec("condition").forGetter(FireProjectilePower::condition)
    ).apply(i, FireProjectilePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
