package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntityOriginAttachment {
    public static final MapCodec<EntityOriginAttachment> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CollectionCodecs.ofAutoIgnore(Layer.CODEC, Origin.CODEC).fieldOf("origins").forGetter(EntityOriginAttachment::getOrigins),
            CollectionCodecs.multiMapCodec(Identifier.CODEC, Power.CODEC).fieldOf("powers").forGetter(EntityOriginAttachment::getPowers),
            CollectionCodecs.ofAutoIgnore(Identifier.CODEC, CollectionCodecs.classMapCodec(PowerComponent.CODEC)).fieldOf("components").forGetter(EntityOriginAttachment::getComponents)
    ).apply(i, EntityOriginAttachment::new));
    public static final Codec<EntityOriginAttachment> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOriginAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Map<Holder<Layer>, Holder<Origin>> origins = new LinkedHashMap<>();
    private final Multimap<Identifier, Holder<Power>> powers = HashMultimap.create();
    private final Map<Identifier, Map<Class<? extends PowerComponent>, PowerComponent>> components = new LinkedHashMap<>();
    private boolean selecting = false;

    public EntityOriginAttachment() {
    }

    private EntityOriginAttachment(Map<Holder<Layer>, Holder<Origin>> origins, Multimap<Identifier, Holder<Power>> powers, Map<Identifier, Map<Class<? extends PowerComponent>, PowerComponent>> components) {
        this.origins.putAll(origins);
        this.powers.putAll(powers);
        this.components.putAll(components);
    }

    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return this.origins;
    }

    public Multimap<Identifier, Holder<Power>> getPowers() {
        return this.powers;
    }

    public Map<Identifier, Map<Class<? extends PowerComponent>, PowerComponent>> getComponents() {
        return this.components;
    }

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }
}
