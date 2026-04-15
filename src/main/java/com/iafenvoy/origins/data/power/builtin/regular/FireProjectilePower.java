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

public class FireProjectilePower extends Power {
    public static final MapCodec<FireProjectilePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(FireProjectilePower::getCooldown),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("entity_type").forGetter(FireProjectilePower::getEntityType),
            Codec.FLOAT.optionalFieldOf("speed", 1.5F).forGetter(FireProjectilePower::getSpeed),
            Codec.FLOAT.optionalFieldOf("divergence", 1F).forGetter(FireProjectilePower::getDivergence),
            ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(FireProjectilePower::getSound),
            EntityCondition.optionalCodec("condition").forGetter(FireProjectilePower::getCondition)
    ).apply(i, FireProjectilePower::new));
    private final int cooldown;
    private final Optional<EntityType<?>> entityType;
    private final float speed;
    private final float divergence;
    private final Optional<ResourceLocation> sound;
    private final EntityCondition condition;

    public FireProjectilePower(BaseSettings settings, int cooldown, Optional<EntityType<?>> entityType, float speed, float divergence, Optional<ResourceLocation> sound, EntityCondition condition) {
        super(settings);
        this.cooldown = cooldown;
        this.entityType = entityType;
        this.speed = speed;
        this.divergence = divergence;
        this.sound = sound;
        this.condition = condition;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public Optional<EntityType<?>> getEntityType() {
        return this.entityType;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getDivergence() {
        return this.divergence;
    }

    public Optional<ResourceLocation> getSound() {
        return this.sound;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
