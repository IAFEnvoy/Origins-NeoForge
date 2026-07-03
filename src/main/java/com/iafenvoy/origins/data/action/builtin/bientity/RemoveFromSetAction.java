package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RemoveFromSetAction(ResourceLocation set) implements BiEntityAction {
    public static final MapCodec<RemoveFromSetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WildcardCodec.INSTANCE.fieldOf("set").forGetter(RemoveFromSetAction::set)
    ).apply(i, RemoveFromSetAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        PowerHelper.get(source).getComponentHolder(this.set, EntitySetComponent.class).ifPresent(x -> x.removeEntity(this.set, target));
    }
}
