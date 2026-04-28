package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DamageAction(Holder<DamageType> damageType, float amount,
                           List<Modifier> modifiers) implements EntityAction {
    public static final MapCodec<DamageAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageAction::damageType),
            Codec.FLOAT.fieldOf("amount").forGetter(DamageAction::amount),
            CombinedCodecs.MODIFIER.optionalFieldOf("modifier", List.of()).forGetter(DamageAction::modifiers)
    ).apply(i, DamageAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        float amount = this.amount;
        if (!this.modifiers.isEmpty() && source instanceof LivingEntity living)
            amount = Modifier.applyModifiers(OriginDataHolder.get(living), this.modifiers, living.getMaxHealth());
        source.hurt(new DamageSource(this.damageType), amount);
    }
}
