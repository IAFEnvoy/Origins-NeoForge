package com.iafenvoy.origins.data.condition.builtin.damage;

import com.iafenvoy.origins.data.condition.DamageCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;

public record DamageInTagCondition(TagKey<DamageType> tag) implements DamageCondition {
    public static final MapCodec<DamageInTagCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("tag").forGetter(DamageInTagCondition::tag)
    ).apply(i, DamageInTagCondition::new));

    @Override
    public @NotNull MapCodec<? extends DamageCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull DamageSource source, float amount) {
        return source.is(this.tag);
    }
}
