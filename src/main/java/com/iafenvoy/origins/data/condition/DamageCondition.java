package com.iafenvoy.origins.data.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public interface DamageCondition {
    Codec<DamageCondition> CODEC = ConditionRegistries.DAMAGE_CONDITION.byNameCodec().dispatch("type", DamageCondition::codec, x -> x);

    static MapCodec<DamageCondition> optionalCodec(String name) {
        return CODEC.optionalFieldOf(name, EmptyCondition.INSTANCE);
    }

    @NotNull
    MapCodec<? extends DamageCondition> codec();

    boolean test(@NotNull DamageSource source, float amount);
}
