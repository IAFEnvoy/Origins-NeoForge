package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddEffectAction(Either<MobEffectInstance, List<MobEffectInstance>> effect) implements EntityAction {
    public static final MapCodec<AddEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.MOB_EFFECT_INSTANCE.optionalFieldOf("effect", Either.right(List.of())).forGetter(AddEffectAction::effect)
    ).apply(i, AddEffectAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source instanceof LivingEntity living)
            this.effect.map(List::of, x -> x).stream().map(MobEffectInstance::new).forEach(living::addEffect);
    }
}
