package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ProjectileCondition(Optional<EntityType<?>> projectile,
                                  EntityCondition projectileCondition) implements DamageCondition {
    public static final MapCodec<ProjectileCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("projectile").forGetter(ProjectileCondition::projectile),
            EntityCondition.optionalCodec("projectile_condition").forGetter(ProjectileCondition::projectileCondition)
    ).apply(i, ProjectileCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        Entity entitySource = source.getEntity();
        return source.is(DamageTypeTags.IS_PROJECTILE)
                && entitySource != null
                && this.projectile.map(entitySource.getType()::equals).orElse(true)
                && this.projectileCondition.test(entitySource);
    }
}
