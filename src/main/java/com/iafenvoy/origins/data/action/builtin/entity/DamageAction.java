package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DamageAction(Holder<DamageType> damageType, float amount, List<Modifier> modifiers) implements EntityAction {
    public static final MapCodec<DamageAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageAction::damageType),
            Codec.FLOAT.fieldOf("amount").forGetter(DamageAction::amount),
            ListConfiguration.MODIFIER_CODEC.forGetter(DamageAction::modifiers)
    ).apply(i, DamageAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        float finalAmount = (float) ModifierUtil.applyModifiers(modifiers, amount);
        source.hurt(new DamageSource(this.damageType), finalAmount);
    }
}
