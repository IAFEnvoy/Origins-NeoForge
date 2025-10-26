package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityIfElseAction(EntityCondition condition, EntityAction ifAction,
                                 EntityAction elseAction) implements EntityAction {
    public static final MapCodec<EntityIfElseAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("condition").forGetter(EntityIfElseAction::condition),
            EntityAction.CODEC.fieldOf("if_action").forGetter(EntityIfElseAction::ifAction),
            EntityAction.optionalCodec("else_action").forGetter(EntityIfElseAction::elseAction)
    ).apply(i, EntityIfElseAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (this.condition.test(source)) this.ifAction.execute(source);
        else this.elseAction.execute(source);
    }
}
