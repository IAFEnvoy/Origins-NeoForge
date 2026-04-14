package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.AutoIgnoreMapCodec;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class EntityOriginAttachment {
    private static final Codec<Map<Holder<Layer>, Holder<Origin>>> ORIGINS_CODEC = new AutoIgnoreMapCodec<>(Layer.CODEC, Origin.CODEC);
    private static final Codec<Map<ResourceLocation, Integer>> RESOURCES_CODEC = new AutoIgnoreMapCodec<>(ResourceLocation.CODEC, Codec.INT);
    public static final Codec<EntityOriginAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
            ORIGINS_CODEC.fieldOf("origin").forGetter(EntityOriginAttachment::getOrigins),
            CollectionCodecs.multiMapCodec(ResourceLocation.CODEC, Power.CODEC).fieldOf("sources").forGetter(EntityOriginAttachment::getSources),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT)).fieldOf("entity_sets").forGetter(EntityOriginAttachment::getEntitySets),
            RESOURCES_CODEC.fieldOf("resources").forGetter(EntityOriginAttachment::getResources)
    ).apply(i, EntityOriginAttachment::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOriginAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Map<Holder<Layer>, Holder<Origin>> origins = new LinkedHashMap<>();
    private final Multimap<ResourceLocation, Holder<Power>> sources = HashMultimap.create();
    private final Map<ResourceLocation, Map<UUID, Integer>> entitySets = new HashMap<>();
    private final Object2IntMap<ResourceLocation> resources = new Object2IntOpenHashMap<>();
    private boolean selecting = false;

    public EntityOriginAttachment() {
    }

    private EntityOriginAttachment(Map<Holder<Layer>, Holder<Origin>> origins, Multimap<ResourceLocation, Holder<Power>> sources, Map<ResourceLocation, Map<UUID, Integer>> entitySets, Map<ResourceLocation, Integer> resources) {
        this.origins.putAll(origins);
        this.sources.putAll(sources);
        this.entitySets.putAll(entitySets);
        this.resources.putAll(resources);
    }

    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return this.origins;
    }

    public Multimap<ResourceLocation, Holder<Power>> getSources() {
        return this.sources;
    }

    public Map<ResourceLocation, Map<UUID, Integer>> getEntitySets() {
        return this.entitySets;
    }

    public Object2IntMap<ResourceLocation> getResources() {
        return this.resources;
    }

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }
}
