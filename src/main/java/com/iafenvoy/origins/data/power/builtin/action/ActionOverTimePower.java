package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActionOverTimePower extends Power {
    public static final MapCodec<ActionOverTimePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOverTimePower::getEntityAction),
            EntityAction.optionalCodec("active_action").forGetter(ActionOverTimePower::getActiveAction),
            EntityAction.optionalCodec("inactive_action").forGetter(ActionOverTimePower::getInactiveAction),
            Codec.INT.optionalFieldOf("interval", 20).forGetter(ActionOverTimePower::getInterval)
    ).apply(i, ActionOverTimePower::new));
    private final EntityAction entityAction;
    private final EntityAction activeAction;
    private final EntityAction inactiveAction;
    private final int interval;

    public ActionOverTimePower(BaseSettings settings, EntityAction entityAction, EntityAction activeAction, EntityAction inactiveAction, int interval) {
        super(settings);
        this.entityAction = entityAction;
        this.activeAction = activeAction;
        this.inactiveAction = inactiveAction;
        this.interval = interval;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public EntityAction getActiveAction() {
        return this.activeAction;
    }

    public EntityAction getInactiveAction() {
        return this.inactiveAction;
    }

    public int getInterval() {
        return this.interval;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        super.active(holder);
        this.activeAction.execute(holder.getEntity());
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        super.inactive(holder);
        this.inactiveAction.execute(holder.getEntity());
    }

    @Override
    public void activeTick(@NotNull OriginDataHolder holder) {
        super.activeTick(holder);
        this.entityAction.execute(holder.getEntity());
    }

    @Override
    public int tickInterval() {
        return this.interval;
    }
}
