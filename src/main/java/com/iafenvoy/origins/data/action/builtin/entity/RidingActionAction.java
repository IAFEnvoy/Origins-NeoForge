package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RidingActionAction(Optional<EntityAction> action, Optional<BiEntityAction> biEntityAction,
                                 Optional<BiEntityCondition> biEntityCondition,
                                 boolean recursive) implements EntityAction {
    public static final MapCodec<RidingActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.optionalFieldOf("action").forGetter(RidingActionAction::action),
            BiEntityAction.CODEC.optionalFieldOf("bientity_action").forGetter(RidingActionAction::biEntityAction),
            BiEntityCondition.CODEC.optionalFieldOf("bientity_condition").forGetter(RidingActionAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("recursive", false).forGetter(RidingActionAction::recursive)
    ).apply(i, RidingActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        Entity vehicle = source.getVehicle();
        if (vehicle != null && this.biEntityCondition.map(x -> x.test(source, vehicle)).orElse(true)) {
            this.action.ifPresent(x -> x.accept(vehicle));
            this.biEntityAction.ifPresent(x -> x.accept(source, vehicle));
            if (this.recursive) this.accept(vehicle);
        }
    }
}
