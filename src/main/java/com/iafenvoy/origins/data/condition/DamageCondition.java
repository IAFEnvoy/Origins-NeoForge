package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface DamageCondition extends BiPredicate<DamageSource, Float> {
    Codec<DamageCondition> CODEC = ConditionRegistries.DAMAGE_CONDITION.byNameCodec().dispatch("type", DamageCondition::codec, x -> x);

    @NotNull
    MapCodec<? extends DamageCondition> codec();

    @Override
    boolean test(@NotNull DamageSource source, @NotNull Float amount);
}
