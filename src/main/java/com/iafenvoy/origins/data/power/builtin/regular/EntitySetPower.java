package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class EntitySetPower extends Power {
    public static final MapCodec<EntitySetPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityAction.optionalCodec("action_on_add").forGetter(EntitySetPower::getActionOnAdd),
            BiEntityAction.optionalCodec("action_on_remove").forGetter(EntitySetPower::getActionOnRemove)
    ).apply(i, EntitySetPower::new));
    private final BiEntityAction actionOnAdd;
    private final BiEntityAction actionOnRemove;

    public EntitySetPower(BaseSettings settings, BiEntityAction actionOnAdd, BiEntityAction actionOnRemove) {
        super(settings);
        this.actionOnAdd = actionOnAdd;
        this.actionOnRemove = actionOnRemove;
    }

    public BiEntityAction getActionOnAdd() {
        return this.actionOnAdd;
    }

    public BiEntityAction getActionOnRemove() {
        return this.actionOnRemove;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void createComponents(ComponentCollector collector) {
        super.createComponents(collector);
        collector.add(new EntitySetComponent());
    }
}
