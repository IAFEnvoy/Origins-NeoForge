package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record ModifyDeathTicksAction(Modifier modifier) implements EntityAction {
    public static final MapCodec<ModifyDeathTicksAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifyDeathTicksAction::modifier)
    ).apply(i, ModifyDeathTicksAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof LivingEntity living)
            living.deathTime = this.modifier.apply(living.deathTime);
    }
}
