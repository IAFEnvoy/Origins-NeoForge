package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public interface DamageCondition extends BiPredicate<DamageSource, Float> {
    Codec<DamageCondition> CODEC = ConditionRegistries.DAMAGE_CONDITION.byNameCodec().dispatch("type", DamageCondition::type, ConditionType::codec);

    ConditionType<DamageCondition> type();

    @Override
    boolean test(DamageSource source, @NotNull Float amount);
}
