package com.iafenvoy.origins.event;

import com.iafenvoy.origins.data.power.Power;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class GrantPowerEvent extends EntityEvent {
    private final Holder<Power> power;
    private final ResourceLocation source;

    public GrantPowerEvent(Entity entity, Holder<Power> power, ResourceLocation source) {
        super(entity);
        this.power = power;
        this.source = source;
    }

    public Holder<Power> getPower() {
        return this.power;
    }

    public ResourceLocation getSource() {
        return this.source;
    }
}
