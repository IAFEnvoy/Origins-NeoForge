package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

//FIXME::Don't use holder?
public record DamageNameCondition(String name) implements DamageCondition {
    public static final MapCodec<DamageNameCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(DamageNameCondition::name)
    ).apply(i, DamageNameCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return source.getMsgId().equals(this.name);
    }
}
