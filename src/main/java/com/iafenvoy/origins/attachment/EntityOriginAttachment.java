package com.iafenvoy.origins.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class EntityOriginAttachment {
    public static final Codec<EntityOriginAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(ResourceLocation.CODEC, OriginRegistries.ORIGIN_CODEC).fieldOf("origin").forGetter(EntityOriginAttachment::getOrigins)
    ).apply(i, EntityOriginAttachment::new));
    private final Map<ResourceLocation, Holder<Origin>> origins = new LinkedHashMap<>();
    //Will not save
    private final Multimap<ResourceLocation, Power> powerMap = HashMultimap.create();

    public EntityOriginAttachment() {
    }

    private EntityOriginAttachment(Map<ResourceLocation, Holder<Origin>> origins) {
        this.origins.putAll(origins);
        this.refreshPowerMap();
    }

    public void setOrigin(ResourceLocation layer, @NotNull Holder<Origin> origin, @NotNull Entity entity) {
        this.clearOrigin(layer, entity);
        this.origins.put(layer, origin);
        this.refreshPowerMap();
        executeOnPowers(origin, p -> p.grant(entity));
    }

    public void clearOrigin(ResourceLocation layer, @NotNull Entity entity) {
        executeOnPowers(this.origins.remove(layer), p -> p.revoke(entity));
    }

    public void refreshPowerMap() {
        this.powerMap.clear();
        this.origins.values().forEach(o -> o.value().powers().forEach(p -> this.powerMap.put(PowerRegistries.POWER_TYPE.getKey(p.codec()), p)));
    }

    public void tick(@NotNull Entity entity) {
        this.origins.values().forEach(o -> executeOnPowers(o, p -> p.tick(entity)));
    }

    public Map<ResourceLocation, Holder<Origin>> getOrigins() {
        return this.origins;
    }

    @NotNull
    public <T extends Power> Collection<Power> getPowers(DeferredHolder<Power, T> holder) {
        return this.getPowers(holder.getId());
    }

    @NotNull
    public Collection<Power> getPowers(ResourceLocation id) {
        return this.powerMap.get(id);
    }

    private static void executeOnPowers(@Nullable Holder<Origin> origin, Consumer<Power> consumer) {
        if (origin != null) origin.value().powers().forEach(consumer);
    }
}
