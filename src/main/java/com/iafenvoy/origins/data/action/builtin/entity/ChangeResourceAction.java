package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

//FIXME::Same to ModifyResourceAction?
public record ChangeResourceAction(ResourceLocation resource, int change,
                                   ResourceOperation operation) implements EntityAction {
    public static final MapCodec<ChangeResourceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(ChangeResourceAction::resource),
            Codec.INT.fieldOf("change").forGetter(ChangeResourceAction::change),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(ChangeResourceAction::operation)
    ).apply(i, ChangeResourceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        OriginDataHolder.get(source).getComponent(this.resource, ResourceComponent.class).ifPresent(x -> x.updateResource(this.operation.getOperator(), this.change));
    }
}
