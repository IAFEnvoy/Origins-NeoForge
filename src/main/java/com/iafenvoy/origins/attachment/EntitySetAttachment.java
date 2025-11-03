package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.registry.OriginsAttachments;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@EventBusSubscriber
public final class EntitySetAttachment {
    public static final Codec<EntitySetAttachment> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT)).xmap(EntitySetAttachment::new, EntitySetAttachment::getStoredEntities);
    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySetAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Map<ResourceLocation, Map<UUID, Integer>> storedEntities = new LinkedHashMap<>();

    public EntitySetAttachment() {
    }

    private EntitySetAttachment(Map<ResourceLocation, Map<UUID, Integer>> storedEntities) {
        for (Map.Entry<ResourceLocation, Map<UUID, Integer>> entry : storedEntities.entrySet())
            this.storedEntities.put(entry.getKey(), new LinkedHashMap<>(entry.getValue()));
    }

    //-1 for unlimited
    public void addEntity(Entity self, ResourceLocation id, Entity target, int timeLimit) {
        Map<UUID, Integer> map = this.storedEntities.computeIfAbsent(id, i -> new LinkedHashMap<>());
        if (!map.containsKey(target.getUUID())) {
            map.put(target.getUUID(), timeLimit);
            this.postAdd(self, id, target);
        }
    }

    public void removeEntity(Entity self, ResourceLocation id, Entity target) {
        Map<UUID, Integer> map = this.storedEntities.computeIfAbsent(id, i -> new LinkedHashMap<>());
        if (map.containsKey(target.getUUID())) {
            map.remove(target.getUUID());
            this.postRemove(self, id, target);
        }
    }

    public void postAdd(Entity self, ResourceLocation id, Entity target) {
        EntityOriginAttachment.get(self).streamEntitySetPowers(id, self.registryAccess()).forEach(x -> x.actionOnAdd().execute(self, target));
    }

    public void postRemove(Entity self, ResourceLocation id, Entity target) {
        EntityOriginAttachment.get(self).streamEntitySetPowers(id, self.registryAccess()).forEach(x -> x.actionOnRemove().execute(self, target));
    }

    public List<UUID> getEntityUuids(ResourceLocation id) {
        return new LinkedList<>(this.storedEntities.get(id).keySet());
    }

    public boolean containEntity(ResourceLocation id, Entity target) {
        return this.storedEntities.computeIfAbsent(id, i -> new LinkedHashMap<>()).containsKey(target.getUUID());
    }

    public int getSize(ResourceLocation id) {
        return this.storedEntities.get(id).size();
    }

    public void tick(@NotNull Entity entity) {
        for (Map.Entry<ResourceLocation, Map<UUID, Integer>> entry : this.storedEntities.entrySet()) {
            List<UUID> removal = new LinkedList<>();
            for (Map.Entry<UUID, Integer> e : entry.getValue().entrySet()) {
                int value = e.getValue();
                if (value == 0) {
                    removal.add(e.getKey());
                    if (entity.level() instanceof ServerLevel serverLevel)
                        this.postRemove(entity, entry.getKey(), serverLevel.getEntity(e.getKey()));
                } else if (value > 0) entry.getValue().computeIfPresent(e.getKey(), (u, i) -> i - 1);
            }
            removal.forEach(entry.getValue()::remove);
        }
    }

    private Map<ResourceLocation, Map<UUID, Integer>> getStoredEntities() {
        return this.storedEntities;
    }

    public static EntitySetAttachment get(Entity entity) {
        return entity.getData(OriginsAttachments.ENTITY_SET);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        EntitySetAttachment.get(event.getEntity()).tick(event.getEntity());
    }
}
