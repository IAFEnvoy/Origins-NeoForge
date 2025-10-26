package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record PassengerActionAction(EntityAction action, BiEntityAction biEntityAction,
                                    BiEntityCondition biEntityCondition, boolean recursive) implements EntityAction {
    public static final MapCodec<PassengerActionAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("action").forGetter(PassengerActionAction::action),
            BiEntityAction.optionalCodec("bientity_action").forGetter(PassengerActionAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(PassengerActionAction::biEntityCondition),
            Codec.BOOL.optionalFieldOf("recursive", false).forGetter(PassengerActionAction::recursive)
    ).apply(i, PassengerActionAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        for (Entity vehicle : source.getPassengers())
            if (this.biEntityCondition.test(source, vehicle)) {
                this.action.execute(vehicle);
                this.biEntityAction.execute(source, vehicle);
                if (this.recursive) this.execute(vehicle);
            }
    }
}
