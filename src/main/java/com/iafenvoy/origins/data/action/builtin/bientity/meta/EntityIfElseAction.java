package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EntityIfElseAction(BiEntityCondition condition, BiEntityAction ifAction,
                                 Optional<BiEntityAction> elseAction) implements BiEntityAction {
    public static final MapCodec<EntityIfElseAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.CODEC.fieldOf("condition").forGetter(EntityIfElseAction::condition),
            BiEntityAction.CODEC.fieldOf("if_action").forGetter(EntityIfElseAction::ifAction),
            BiEntityAction.CODEC.optionalFieldOf("else_action").forGetter(EntityIfElseAction::elseAction)
    ).apply(i, EntityIfElseAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source, @NotNull Entity target) {
        if (this.condition.test(source, target)) this.ifAction.accept(source, target);
        else this.elseAction.ifPresent(x -> x.accept(source, target));
    }
}
