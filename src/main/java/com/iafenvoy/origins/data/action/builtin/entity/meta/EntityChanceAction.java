package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EntityChanceAction(EntityAction action, float chance,
                                 Optional<EntityAction> failAction) implements EntityAction {
    public static final MapCodec<EntityChanceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(EntityChanceAction::action),
            Codec.FLOAT.fieldOf("chance").forGetter(EntityChanceAction::chance),
            EntityAction.CODEC.optionalFieldOf("fail_action").forGetter(EntityChanceAction::failAction)
    ).apply(i, EntityChanceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (Math.random() < this.chance) this.action.accept(source);
        else this.failAction.ifPresent(x -> x.accept(source));
    }
}
