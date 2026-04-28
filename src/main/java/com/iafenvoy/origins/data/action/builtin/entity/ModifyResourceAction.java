package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyResourceAction(Modifier modifier, ResourceLocation resource) implements EntityAction {
    public static final MapCodec<ModifyResourceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Modifier.CODEC.fieldOf("modifier").forGetter(ModifyResourceAction::modifier),
            ResourceLocation.CODEC.fieldOf("resource").forGetter(ModifyResourceAction::resource)
    ).apply(i, ModifyResourceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        OriginDataHolder holder = OriginDataHolder.get(source);
        holder.getComponent(this.resource, ResourceComponent.class).ifPresent(x -> x.updateResource(y -> Modifier.applyModifiers(holder, List.of(this.modifier), y)));
    }
}
