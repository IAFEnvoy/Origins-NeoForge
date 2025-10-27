package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface DamageCondition {
    Codec<DamageCondition> CODEC = DefaultedCodec.registryDispatch(ConditionRegistries.DAMAGE_CONDITION, DamageCondition::codec, Function.identity(), () -> EmptyCondition.INSTANCE);

    static MapCodec<DamageCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends DamageCondition> codec();

    boolean test(@NotNull DamageSource source, float amount);
}
