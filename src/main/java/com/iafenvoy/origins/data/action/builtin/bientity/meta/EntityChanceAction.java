package com.iafenvoy.origins.data.action.builtin.bientity.meta;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityChanceAction(BiEntityAction action, float chance,
                                 BiEntityAction failAction) implements BiEntityAction {
    public static final MapCodec<EntityChanceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(EntityChanceAction::action),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(EntityChanceAction::chance),
            BiEntityAction.optionalCodec("fail_action").forGetter(EntityChanceAction::failAction)
    ).apply(i, EntityChanceAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        if (Math.random() < this.chance) this.action.execute(source, target);
        else this.failAction.execute(source, target);
    }
}
