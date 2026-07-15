package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record SetNoGravityAction(boolean noGravity) implements EntityAction {
    public static final MapCodec<SetNoGravityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("no_gravity", true).forGetter(SetNoGravityAction::noGravity)
    ).apply(instance, SetNoGravityAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        source.setNoGravity(this.noGravity);
    }
}
