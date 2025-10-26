package com.iafenvoy.origins.data.action.builtin.entity.meta;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityChanceAction(EntityAction action, float chance, EntityAction failAction) implements EntityAction {
    public static final MapCodec<EntityChanceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(EntityChanceAction::action),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(EntityChanceAction::chance),
            EntityAction.optionalCodec("fail_action").forGetter(EntityChanceAction::failAction)
    ).apply(i, EntityChanceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (Math.random() < this.chance) this.action.execute(source);
        else this.failAction.execute(source);
    }
}
