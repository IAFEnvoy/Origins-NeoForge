package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ClearSetAction(ResourceLocation set) implements EntityAction {
    public static final MapCodec<ClearSetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WildcardCodec.INSTANCE.fieldOf("set").forGetter(ClearSetAction::set)
    ).apply(i, ClearSetAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source.level() instanceof ServerLevel level)
            PowerHelper.get(source).getComponentHolder(this.set, EntitySetComponent.class).ifPresent(x -> x.removeAllEntities(level));
    }
}
