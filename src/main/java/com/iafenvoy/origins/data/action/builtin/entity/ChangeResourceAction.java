package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.helper.ResourceValueHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.iafenvoy.origins.util.math.ResourceOperation;
import com.iafenvoy.origins.util.math.ResourceReference;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record ChangeResourceAction(ResourceLocation resource, ResourceReference change,
                                   ResourceOperation operation) implements EntityAction {
    public static final MapCodec<ChangeResourceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WildcardCodec.INSTANCE.fieldOf("resource").forGetter(ChangeResourceAction::resource),
            ResourceReference.CODEC.fieldOf("change").forGetter(ChangeResourceAction::change),
            ResourceOperation.CODEC.optionalFieldOf("operation", ResourceOperation.ADD).forGetter(ChangeResourceAction::operation)
    ).apply(i, ChangeResourceAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (!(source instanceof LivingEntity)) return;
        double change = this.change.resolve(source);
        if (this.operation == ResourceOperation.ADD)
            ResourceValueHelper.add(source, this.resource, change);
        else
            ResourceValueHelper.set(source, this.resource, change);
    }
}
