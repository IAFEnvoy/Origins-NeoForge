package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AttackerDamageCondition(EntityCondition entityCondition) implements DamageCondition {
    public static final MapCodec<AttackerDamageCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("entity_condition").forGetter(AttackerDamageCondition::entityCondition)
    ).apply(i, AttackerDamageCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        return attacker != null && this.entityCondition.test(attacker);
    }
}
