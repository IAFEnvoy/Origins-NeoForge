package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EntitySetAttachment {
    public static final Codec<EntitySetAttachment> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, UUIDUtil.CODEC.listOf()).xmap(EntitySetAttachment::new, EntitySetAttachment::getStoredEntities);
    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySetAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Multimap<ResourceLocation, UUID> storedEntities = HashMultimap.create();

    public EntitySetAttachment() {
    }

    private EntitySetAttachment(Map<ResourceLocation, List<UUID>> storedEntities) {
        for (Map.Entry<ResourceLocation, List<UUID>> entry : storedEntities.entrySet())
            this.storedEntities.putAll(entry.getKey(), entry.getValue());
    }

    public void addEntity(Entity self, ResourceLocation id, Entity target) {
        if (!this.storedEntities.get(id).contains(target.getUUID())) {
            this.storedEntities.put(id, target.getUUID());
            EntityOriginAttachment.get(self).streamEntitySetPowers().filter(x -> x.getId(self.registryAccess()).equals(id)).forEach(x -> x.actionOnAdd().execute(self, target));
        }
    }

    public void removeEntity(Entity self, ResourceLocation id, Entity target) {
        if (this.storedEntities.remove(id, target.getUUID()))
            EntityOriginAttachment.get(self).streamEntitySetPowers().filter(x -> x.getId(self.registryAccess()).equals(id)).forEach(x -> x.actionOnRemove().execute(self, target));
    }

    public boolean containEntity(ResourceLocation id, Entity target) {
        return this.storedEntities.get(id).contains(target.getUUID());
    }

    public int getSize(ResourceLocation id) {
        return this.storedEntities.get(id).size();
    }

    private Map<ResourceLocation, List<UUID>> getStoredEntities() {
        ImmutableMap.Builder<ResourceLocation, List<UUID>> builder = ImmutableMap.builder();
        for (ResourceLocation rl : this.storedEntities.keySet())
            builder.put(rl, List.copyOf(this.storedEntities.get(rl)));
        return builder.build();
    }

    public static EntitySetAttachment get(Entity entity) {
        return entity.getData(OriginsAttachments.ENTITY_SET);
    }
}
