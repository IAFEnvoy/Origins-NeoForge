package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class FireDamageCondition implements DamageCondition {
    public static final FireDamageCondition INSTANCE = new FireDamageCondition();
    public static final MapCodec<FireDamageCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE);
    }
}
