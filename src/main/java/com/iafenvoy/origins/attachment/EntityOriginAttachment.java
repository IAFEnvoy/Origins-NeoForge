package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.util.RandomHelper;
import com.iafenvoy.origins.util.codec.AutoIgnoreMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class EntityOriginAttachment {
    public static final Codec<Map<Holder<Layer>, Holder<Origin>>> ORIGINS_CODEC = new AutoIgnoreMapCodec<>(Layer.CODEC, Origin.CODEC);
    public static final Codec<EntityOriginAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
            ORIGINS_CODEC.fieldOf("origin").forGetter(EntityOriginAttachment::getOrigins)
    ).apply(i, EntityOriginAttachment::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOriginAttachment> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private final Map<Holder<Layer>, Holder<Origin>> origins = new LinkedHashMap<>();
    private boolean selecting = false;
    //Will not save
    private final Multimap<ResourceLocation, Power> powerMap = HashMultimap.create();

    public EntityOriginAttachment() {
    }

    private EntityOriginAttachment(Map<Holder<Layer>, Holder<Origin>> origins) {
        this.origins.putAll(origins);
        this.refreshPowerMap();
    }

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin, @NotNull Entity entity) {
        this.clearOrigin(layer, entity);
        if (origin.value() == Origin.EMPTY) return;
        this.origins.put(layer, origin);
        this.refreshPowerMap();
        executeOnPowers(origin, p -> p.grant(entity));
    }

    public void clearOrigin(@NotNull Holder<Layer> layer, @NotNull Entity entity) {
        executeOnPowers(this.origins.remove(layer), p -> p.revoke(entity));
    }

    public void refreshPowerMap() {
        this.powerMap.clear();
        this.origins.values().forEach(o -> o.value().powers().stream().map(Holder::value).forEach(p -> this.powerMap.put(PowerRegistries.POWER_TYPE.getKey(p.codec()), p)));
    }

    public void tick(@NotNull Entity entity) {
        this.origins.values().forEach(o -> executeOnPowers(o, p -> p.tick(entity)));
    }

    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return this.origins;
    }

    public Holder<Origin> getOrigin(Holder<Layer> layer) {
        return this.origins.get(layer);
    }

    @NotNull
    public <T extends Power> Collection<T> getPowers(DeferredHolder<MapCodec<? extends Power>, MapCodec<T>> holder, Class<T> clazz) {
        return this.getPowers(holder.getId(), clazz);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        List<T> results = new LinkedList<>();
        for (Power power : this.powerMap.get(id))
            if (power != null && clazz.isAssignableFrom(power.getClass()))
                results.add(clazz.cast(power));
        return results;
    }

    public boolean hasOrigin(Holder<Layer> layer) {
        return this.origins.containsKey(layer) && this.origins.get(layer).value() != Origin.EMPTY;
    }

    public void sync(Entity entity) {
        entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void fillAutoChoosing(Entity entity) {
        boolean changed = false;
        List<Holder<Layer>> layers = LayerRegistries.streamAutoChooseLayers(entity.registryAccess()).toList();
        for (Holder<Layer> layer : layers) {
            if (this.origins.containsKey(layer)) continue;
            List<Holder<Origin>> available = layer.value().collectRandomizableOrigins(entity.registryAccess()).toList();
            if (!available.isEmpty()) {
                this.setOrigin(layer, RandomHelper.randomOne(available), entity);
                changed = true;
            }
        }
        if (changed) this.sync(entity);
    }

    public boolean hasAllOrigins(RegistryAccess access) {
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(access).toList();
        for (Holder<Layer> layer : layers) {
            if (this.origins.containsKey(layer)) continue;
            return false;
        }
        return true;
    }

    private static void executeOnPowers(@Nullable Holder<Origin> origin, Consumer<Power> consumer) {
        if (origin != null) origin.value().powers().stream().map(Holder::value).forEach(consumer);
    }

    public static EntityOriginAttachment get(Entity entity) {
        return entity.getData(OriginsAttachments.ENTITY_ORIGIN);
    }
}
