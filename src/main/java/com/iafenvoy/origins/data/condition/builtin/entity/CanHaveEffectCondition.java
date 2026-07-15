package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record CanHaveEffectCondition(Holder<MobEffect> effect) implements EntityCondition {
    public static final MapCodec<CanHaveEffectCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(CanHaveEffectCondition::effect)
    ).apply(instance, CanHaveEffectCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof LivingEntity living && living.canBeAffected(new MobEffectInstance(this.effect));
    }
}
