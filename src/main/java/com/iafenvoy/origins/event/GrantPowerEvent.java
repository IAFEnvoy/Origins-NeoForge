package com.iafenvoy.origins.event;

import com.iafenvoy.origins.data.power.Power;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class GrantPowerEvent extends EntityEvent {
    private final Holder<Power> power;
    private final Identifier source;

    public GrantPowerEvent(Entity entity, Holder<Power> power, Identifier source) {
        super(entity);
        this.power = power;
        this.source = source;
    }

    public Holder<Power> getPower() {
        return this.power;
    }

    public Identifier getSource() {
        return this.source;
    }
}
