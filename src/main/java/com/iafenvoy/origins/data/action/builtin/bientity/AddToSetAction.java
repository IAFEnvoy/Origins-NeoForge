package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AddToSetAction(ResourceLocation set, int timeLimit) implements BiEntityAction {
    public static final MapCodec<AddToSetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("set").forGetter(AddToSetAction::set),
            Codec.INT.optionalFieldOf("time_limit", -1).forGetter(AddToSetAction::timeLimit)
    ).apply(i, AddToSetAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        OriginDataHolder.get(source).addEntity(this.set, target, this.timeLimit);
    }
}
