package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
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

public record DamageTargetAction(Holder<DamageType> damageType, float amount, List<Modifier> modifiers) implements BiEntityAction {
    public static final MapCodec<DamageTargetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageTargetAction::damageType),
            Codec.FLOAT.fieldOf("amount").forGetter(DamageTargetAction::amount),
            ListConfiguration.MODIFIER_CODEC.forGetter(DamageTargetAction::modifiers)
    ).apply(i, DamageTargetAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        float finalAmount = (float) ModifierUtil.applyModifiers(modifiers, amount);
        target.hurt(new DamageSource(this.damageType, source), finalAmount);
    }
}
