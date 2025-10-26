package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RidingActionAction(EntityAction action, BiEntityAction biEntityAction,
                                 BiEntityCondition biEntityCondition, boolean recursive) implements EntityAction {
    public static final MapCodec<RidingActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("action").forGetter(RidingActionAction::action),
            BiEntityAction.optionalCodec("bientity_action").forGetter(RidingActionAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RidingActionAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("recursive", false).forGetter(RidingActionAction::recursive)
    ).apply(i, RidingActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        Entity vehicle = source.getVehicle();
        if (vehicle != null && this.biEntityCondition.test(source, vehicle)) {
            this.action.execute(vehicle);
            this.biEntityAction.execute(source, vehicle);
            if (this.recursive) this.execute(vehicle);
        }
    }
}
