package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerReference;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RevokePowerAction(Optional<PowerReference> power,
                                Optional<ResourceLocation> source) implements EntityAction {
    public static final MapCodec<RevokePowerAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            PowerReference.CODEC.optionalFieldOf("power").forGetter(RevokePowerAction::power),
            ResourceLocation.CODEC.optionalFieldOf("source").forGetter(RevokePowerAction::source)
    ).apply(i, RevokePowerAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        OriginDataHolder holder = OriginDataHolder.get(source);
        if (this.power.isPresent() && this.source.isPresent())
            this.power.flatMap(PowerReference::get).ifPresent(power -> OriginDataHolder.get(source).revokePower(this.source.get(), power));
        else {
            this.power.flatMap(PowerReference::get).ifPresent(holder::revokeAllPowers);
            this.source.ifPresent(holder::revokeAllPowers);
        }
    }
}
