package com.iafenvoy.origins.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public final class EntitySetAttachment {
    public static final Codec<EntitySetAttachment> CODEC = UUIDUtil.CODEC.listOf().xmap(EntitySetAttachment::new, x -> x.storedEntities);
    private final List<UUID> storedEntities = new LinkedList<>();

    public EntitySetAttachment() {
    }

    private EntitySetAttachment(List<UUID> storedEntities) {
        this.storedEntities.addAll(storedEntities);
    }

    public void addEntity(Entity self, Entity target) {
        if (!this.storedEntities.contains(target.getUUID())) {
            this.storedEntities.add(target.getUUID());
            //TODO::Post add event
        }
    }

    public void removeEntity(Entity self, Entity target) {
        if (this.storedEntities.remove(target.getUUID())) {
            //TODO::Post remove event
        }
    }
}
