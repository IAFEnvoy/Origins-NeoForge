package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.component.builtin.ToggleComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ToggleAction(ResourceLocation power) implements EntityAction {
    public static final MapCodec<ToggleAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("power").forGetter(ToggleAction::power)
    ).apply(i, ToggleAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        OriginDataHolder.get(source).getComponent(this.power, ToggleComponent.class).ifPresent(ToggleComponent::toggle);
    }
}
