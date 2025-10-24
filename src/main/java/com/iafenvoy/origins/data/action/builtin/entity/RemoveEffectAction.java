package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RemoveEffectAction(Either<Holder<MobEffect>, List<Holder<MobEffect>>> effect) implements EntityAction {
    public static final MapCodec<RemoveEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.MOB_EFFECT.optionalFieldOf("effect", Either.right(List.of())).forGetter(RemoveEffectAction::effect)
    ).apply(i, RemoveEffectAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source instanceof LivingEntity living) {
            List<Holder<MobEffect>> effects = this.effect.map(List::of, x -> x);
            if (effects.isEmpty()) living.removeAllEffects();
            else effects.forEach(living::removeEffect);
        }
    }
}
