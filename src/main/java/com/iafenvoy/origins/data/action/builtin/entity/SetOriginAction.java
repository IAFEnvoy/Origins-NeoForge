package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record SetOriginAction(Holder<Layer> layer, Holder<Origin> origin) implements EntityAction {
    public static final MapCodec<SetOriginAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Layer.CODEC.fieldOf("layer").forGetter(SetOriginAction::layer),
            Origin.CODEC.fieldOf("origin").forGetter(SetOriginAction::origin)
    ).apply(instance, SetOriginAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity entity) {
        if (!entity.level().isClientSide) {
            OriginDataHolder.optional(entity).ifPresent(holder -> {
                holder.setOrigin(this.layer, this.origin);
                holder.sync();
            });
        }
    }
}
