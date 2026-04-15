package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
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
    public static final Codec<EntityOriginAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
            ORIGINS_CODEC.fieldOf("origins").forGetter(EntityOriginAttachment::getOrigins),
            CollectionCodecs.multiMapCodec(ResourceLocation.CODEC, Power.CODEC).fieldOf("powers").forGetter(EntityOriginAttachment::getPowers),
            Codec.unboundedMap(ResourceLocation.CODEC, CollectionCodecs.classMapCodec(PowerComponent.CODEC)).fieldOf("components").forGetter(EntityOriginAttachment::getComponents)
    ).apply(i, EntityOriginAttachment::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOriginAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Map<Holder<Layer>, Holder<Origin>> origins = new LinkedHashMap<>();
    private final Multimap<ResourceLocation, Holder<Power>> powers = HashMultimap.create();
    private final Map<ResourceLocation, Map<Class<? extends PowerComponent>, PowerComponent>> components = new LinkedHashMap<>();
    private boolean selecting = false;

    public EntityOriginAttachment() {
    }

    private EntityOriginAttachment(Map<Holder<Layer>, Holder<Origin>> origins, Multimap<ResourceLocation, Holder<Power>> powers, Map<ResourceLocation, Map<Class<? extends PowerComponent>, PowerComponent>> components) {
        this.origins.putAll(origins);
        this.powers.putAll(powers);
        this.components.putAll(components);
    }

    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return this.origins;
    }

    public Multimap<ResourceLocation, Holder<Power>> getPowers() {
        return this.powers;
    }

    public Map<ResourceLocation, Map<Class<? extends PowerComponent>, PowerComponent>> getComponents() {
        return this.components;
    }

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }
}
