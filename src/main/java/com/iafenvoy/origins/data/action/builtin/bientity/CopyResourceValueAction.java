package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data._common.helper.ResourceValueHelper;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record CopyResourceValueAction(ResourceLocation actorResource, ResourceLocation targetResource,
                                      ResourceOperation operation) implements BiEntityAction {
    public static final MapCodec<CopyResourceValueAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("actor_resource").forGetter(CopyResourceValueAction::actorResource),
            ResourceLocation.CODEC.fieldOf("target_resource").forGetter(CopyResourceValueAction::targetResource),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(CopyResourceValueAction::operation)
    ).apply(i, CopyResourceValueAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        double value = ResourceValueHelper.value(target, this.targetResource);
        if (this.operation == ResourceOperation.ADD)
            ResourceValueHelper.add(source, this.actorResource, value);
        else
            ResourceValueHelper.set(source, this.actorResource, value);
    }
}
