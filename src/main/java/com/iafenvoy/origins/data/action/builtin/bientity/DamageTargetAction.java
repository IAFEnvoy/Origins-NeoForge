package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record DamageTargetAction(Holder<DamageType> damageType, float amount) implements BiEntityAction {
    public static final MapCodec<DamageTargetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageTargetAction::damageType),
            Codec.FLOAT.fieldOf("amount").forGetter(DamageTargetAction::amount)
    ).apply(i, DamageTargetAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        target.hurt(new DamageSource(this.damageType, source), this.amount);
    }
}
