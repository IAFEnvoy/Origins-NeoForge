package com.iafenvoy.origins.event;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class GrantOriginEvent extends EntityEvent {
    private final Holder<Layer> layer;
    private final Holder<Origin> origin;

    public GrantOriginEvent(Entity entity, Holder<Layer> layer, Holder<Origin> origin) {
        super(entity);
        this.layer = layer;
        this.origin = origin;
    }

    public Holder<Layer> getLayer() {
        return this.layer;
    }

    public Holder<Origin> getOrigin() {
        return this.origin;
    }
}
