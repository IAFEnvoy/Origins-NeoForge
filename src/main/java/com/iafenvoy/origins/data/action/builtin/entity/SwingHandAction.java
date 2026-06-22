package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record SwingHandAction(InteractionHand hand) implements EntityAction {
    public static final MapCodec<SwingHandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraEnumCodecs.HAND.fieldOf("hand").forGetter(SwingHandAction::hand)
    ).apply(i, SwingHandAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof LivingEntity living) living.swing(this.hand, true);
    }
}
