package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public final class MagicDamageCondition implements DamageCondition {
    public static final MagicDamageCondition INSTANCE = new MagicDamageCondition();
    public static final MapCodec<MagicDamageCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    private MagicDamageCondition() {
    }

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return source.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && source.is(DamageTypeTags.WITCH_RESISTANT_TO);
    }
}
