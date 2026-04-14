package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.builtin.regular.EntitySetPower;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.util.RandomHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber
public record OriginDataHolder(Entity entity, EntityOriginAttachment data, RegistryAccess access) {
    public static final ResourceLocation DEFAULT_SOURCE = ResourceLocation.withDefaultNamespace("command");

    //Query
    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return Map.copyOf(this.data.getOrigins());
    }

    public Holder<Origin> getOrigin(Holder<Layer> layer) {
        return this.data.getOrigins().get(layer);
    }

    //Power Related
    public void addPower(Holder<Power> power) {
        this.addPower(DEFAULT_SOURCE, power);
    }

    public void addPower(ResourceLocation source, Holder<Power> power) {
        this.data.getSources().put(source, power);
        power.value().grant(this.entity);
    }

    public void removePower(Holder<Power> power) {
        this.removePower(DEFAULT_SOURCE, power);
    }

    public void removePower(ResourceLocation source, Holder<Power> power) {
        this.data.getSources().remove(source, power);
        power.value().revoke(this.entity);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(DeferredHolder<MapCodec<? extends Power>, MapCodec<T>> holder, Class<T> clazz) {
        return this.getPowers(holder.getId(), clazz);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        List<T> results = this.data.getSources().values().stream().filter(x -> x.unwrapKey().map(ResourceKey::location).map(id::equals).orElse(false)).map(Holder::value).toList().stream().filter(power -> power != null && clazz.isAssignableFrom(power.getClass())).map(clazz::cast).collect(Collectors.toCollection(LinkedList::new));
        return Prioritized.class.isAssignableFrom(clazz) ? results.stream().map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast).toList() : results;
    }

    @NotNull
    public <T extends Power> Stream<T> streamPowers(Class<T> clazz) {
        Stream<T> results = this.data.getSources().values().stream().map(Holder::value).filter(power -> clazz.isAssignableFrom(power.getClass())).map(clazz::cast);
        return Prioritized.class.isAssignableFrom(clazz) ? results.map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast) : results;
    }

    //Origin Related
    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
        this.clearOrigin(layer);
        if (origin.value() == Origin.EMPTY) return;
        if (this.entity.level().isClientSide)
            //TODO::Move message outside
            this.entity.sendSystemMessage(Component.translatable("commands.origin.set.success.single", this.entity.getDisplayName(), Layer.getName(layer), Origin.getName(origin)));
        this.data.getOrigins().put(layer, origin);
        ResourceLocation id = origin.getKey().location();
        origin.value().powers().forEach(x -> this.addPower(id, x));
    }

    public void clearOrigin(@NotNull Holder<Layer> layer) {
        Holder<Origin> origin = this.data.getOrigins().remove(layer);
        if (origin == null) return;
        ResourceLocation id = origin.getKey().location();
        origin.value().powers().forEach(x -> this.removePower(id, x));
    }

    public boolean hasOrigin(Holder<Layer> layer) {
        return this.data.getOrigins().containsKey(layer) && this.data.getOrigins().get(layer).value() != Origin.EMPTY;
    }

    public boolean fillAutoChoosing() {
        boolean changed = false;
        List<Holder<Layer>> layers = LayerRegistries.streamAutoChooseLayers(this.entity.registryAccess()).toList();
        for (Holder<Layer> layer : layers) {
            if (this.data.getOrigins().containsKey(layer)) continue;
            changed |= this.randomOrigin(layer);
        }
        if (changed) this.sync();
        return changed;
    }

    public boolean randomOrigin(Holder<Layer> layer) {
        List<Holder<Origin>> available = layer.value().collectRandomizableOrigins(this.entity.registryAccess()).toList();
        if (!available.isEmpty()) {
            @NotNull Holder<Origin> origin = RandomHelper.randomOne(available);
            this.clearOrigin(layer);
            if (origin.value() != Origin.EMPTY) {
                if (this.entity.level().isClientSide)
                    this.entity.sendSystemMessage(Component.translatable("commands.origin.set.success.single", this.entity.getDisplayName(), Layer.getName(layer), Origin.getName(origin)));
                this.setOrigin(layer, origin);
            }
            return true;
        }
        return false;
    }

    public boolean hasAllOrigins() {
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(this.access).toList();
        for (Holder<Layer> layer : layers) {
            if (this.data.getOrigins().containsKey(layer)) continue;
            return false;
        }
        return true;
    }

    //Entity Sets
    public void addEntity(ResourceLocation id, Entity target) {
        this.addEntity(id, target, -1);
    }

    //-1 for unlimited
    public void addEntity(ResourceLocation id, Entity target, int timeLimit) {
        Map<UUID, Integer> map = this.data.getEntitySets().computeIfAbsent(id, i -> new LinkedHashMap<>());
        if (!map.containsKey(target.getUUID())) {
            map.put(target.getUUID(), timeLimit);
            this.postAdd(this.entity, id, target);
        }
    }

    public void removeEntity(ResourceLocation id, Entity target) {
        Map<UUID, Integer> map = this.data.getEntitySets().computeIfAbsent(id, i -> new LinkedHashMap<>());
        if (map.containsKey(target.getUUID())) {
            map.remove(target.getUUID());
            this.postRemove(this.entity, id, target);
        }
    }

    public void postAdd(Entity self, ResourceLocation id, Entity target) {
        this.getPowers(id, EntitySetPower.class).forEach(x -> x.actionOnAdd().execute(self, target));
    }

    public void postRemove(Entity self, ResourceLocation id, Entity target) {
        if (target != null)
            this.getPowers(id, EntitySetPower.class).forEach(x -> x.actionOnRemove().execute(self, target));
    }

    public List<UUID> getEntityUuids(ResourceLocation id) {
        Map<UUID, Integer> map = this.data.getEntitySets().get(id);
        return map != null ? new LinkedList<>(map.keySet()) : new LinkedList<>();
    }

    public boolean containEntity(ResourceLocation id, Entity target) {
        return this.data.getEntitySets().computeIfAbsent(id, i -> new LinkedHashMap<>()).containsKey(target.getUUID());
    }

    public int getSize(ResourceLocation id) {
        Map<UUID, Integer> map = this.data.getEntitySets().get(id);
        return map != null ? map.size() : 0;
    }

    //Resources
    public void updateResource(ResourceLocation id, IntBinaryOperator operation, int value) {
        this.data.getResources().computeInt(id, (i, cur) -> operation.applyAsInt(cur, value));
    }

    public int getResource(ResourceLocation id) {
        return this.data.getResources().getOrDefault(id, 0);
    }

    //Utils
    public static OriginDataHolder get(Entity entity) {
        return new OriginDataHolder(entity, entity.getData(OriginsAttachments.ENTITY_ORIGIN), entity.registryAccess());
    }

    private static void executeOnPowers(@Nullable Holder<Origin> origin, Consumer<Power> consumer) {
        if (origin != null) origin.value().powers().stream().map(Holder::value).forEach(consumer);
    }

    //Ticking
    public void sync() {
        this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void tick(@NotNull Entity entity) {
        this.getOrigins().values().forEach(o -> executeOnPowers(o, p -> p.tick(entity)));
        //Entity Sets
        for (Map.Entry<ResourceLocation, Map<UUID, Integer>> entry : this.data.getEntitySets().entrySet()) {
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

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        OriginDataHolder.get(event.getEntity()).tick(event.getEntity());
    }
}
