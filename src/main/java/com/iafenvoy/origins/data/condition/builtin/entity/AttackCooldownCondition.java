package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record AttackCooldownCondition(Comparison comparison) implements EntityCondition {
    public static final MapCodec<AttackCooldownCondition> CODEC = Comparison.CODEC
            .xmap(AttackCooldownCondition::new, AttackCooldownCondition::comparison);

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return entity instanceof Player player && this.comparison.compare(player.getAttackStrengthScale(0.0F));
    }
}
