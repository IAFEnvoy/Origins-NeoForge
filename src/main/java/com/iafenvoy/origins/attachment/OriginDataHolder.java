package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.util.RLHelper;
import com.iafenvoy.origins.util.RandomHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
    public void grantPower(ResourceLocation source, Holder<Power> power) {
        this.data.getPowers().put(source, power);
        this.data.getComponents().put(RLHelper.id(power), power.value().createComponents().stream().collect(Collectors.toMap(PowerComponent::getClass, Function.identity())));
        power.value().grant(this.entity);
        this.sync();
    }

    public void revokePower(ResourceLocation source, Holder<Power> power) {
        this.data.getPowers().remove(source, power);
        this.data.getComponents().remove(RLHelper.id(power));
        power.value().revoke(this.entity);
        this.sync();
    }

    public void revokeAllPowers(ResourceLocation source) {
        this.data.getPowers().entries().stream().filter(x -> x.getKey().equals(source)).map(Map.Entry::getValue).toList().forEach(p -> this.revokePower(source, p));
    }

    public void revokeAllPowers(Holder<Power> power) {
        this.data.getPowers().values().remove(power);
    }

    //FIXME::Too many stream lol
    @NotNull
    public <T extends Power> List<T> getPowers(DeferredHolder<MapCodec<? extends Power>, MapCodec<T>> holder, Class<T> clazz) {
        return this.getPowers(holder.getId(), clazz);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        List<T> results = this.data.getPowers().values().stream().filter(x -> x.unwrapKey().map(ResourceKey::location).map(id::equals).orElse(false)).map(Holder::value).toList().stream().filter(power -> power != null && clazz.isAssignableFrom(power.getClass())).map(clazz::cast).collect(Collectors.toCollection(LinkedList::new));
        return Prioritized.class.isAssignableFrom(clazz) ? results.stream().map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast).toList() : results;
    }

    //Only for toggle, which need to bypass active logic
    @NotNull
    public <T> Stream<T> streamPowers(Class<T> clazz) {
        Stream<T> results = this.data.getPowers().values().stream().map(Holder::value).filter(power -> clazz.isAssignableFrom(power.getClass())).map(clazz::cast);
        return Prioritized.class.isAssignableFrom(clazz) ? results.map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast) : results;
    }

    @NotNull
    public <T extends Power> Stream<T> streamActivePowers(Class<T> clazz) {
        return this.streamPowers(clazz).filter(x -> x.isActive(this));
    }

    public boolean hasPower(Holder<Power> power) {
        return this.data.getPowers().values().stream().anyMatch(p -> p.equals(power));
    }

    public boolean hasPower(ResourceLocation source, Holder<Power> power) {
        return this.data.getPowers().entries().stream().anyMatch(e -> e.getKey().equals(source) && e.getValue().equals(power));
    }

    public <T extends Power> boolean hasPower(Class<T> clazz, boolean activeOnly) {
        return this.data.getPowers().values().stream().map(Holder::value).filter(x -> !activeOnly || x.isActive(this)).anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
    }

    public void onPowerToggle(String key) {
        this.streamPowers(Toggleable.class).forEach(x -> x.toggle(this, key));
    }

    public <T extends Power> boolean isPowerActive(Class<T> clazz) {
        return this.streamActivePowers(clazz).findAny().isPresent();
    }

    //Origin Related
    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
        this.clearOrigin(layer);
        if (origin.value() == Origin.EMPTY) return;
        if (this.entity.level().isClientSide)
            //TODO::Move message outside
            this.entity.sendSystemMessage(Component.translatable("commands.origin.set.success.single", this.entity.getDisplayName(), Layer.getName(layer), Origin.getName(origin)));
        this.data.getOrigins().put(layer, origin);
        ResourceLocation id = RLHelper.id(origin);
        origin.value().powers().forEach(x -> this.grantPower(id, x));
    }

    public void clearOrigin(@NotNull Holder<Layer> layer) {
        Holder<Origin> origin = this.data.getOrigins().remove(layer);
        if (origin == null) return;
        ResourceLocation id = RLHelper.id(origin);
        this.revokeAllPowers(id);
    }

    public boolean hasOrigin(Holder<Layer> layer, Holder<Origin> origin) {
        return this.data.getOrigins().containsKey(layer) && this.data.getOrigins().get(layer).value().equals(origin.value());
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

    //Component Related
    public <T> List<T> getComponents(Class<T> clazz) {
        return this.data.getComponents().values().stream().map(x -> x.get(clazz)).filter(Objects::nonNull).map(clazz::cast).toList();
    }

    public <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id).get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
        return this.getComponentFor(this.access.registryOrThrow(PowerRegistries.POWER_KEY).wrapAsHolder(power), clazz);
    }

    public <T> Optional<T> getComponentFor(Holder<Power> power, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(RLHelper.id(power)).get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    public <H, T extends ComponentHolderProvider<H>> List<H> getComponentHolders(Class<T> clazz) {
        return this.data.getComponents().values().stream().map(x -> x.get(clazz)).filter(Objects::nonNull).map(clazz::cast).map(x -> x.constructHolder(this)).toList();
    }

    public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id).get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast).map(x -> x.constructHolder(this));
    }

    //Utils
    public static OriginDataHolder get(Entity entity) {
        return new OriginDataHolder(entity, entity.getData(OriginsAttachments.ENTITY_ORIGIN), entity.registryAccess());
    }

    private void executeOnPowers(@Nullable Holder<Origin> origin, Consumer<Power> consumer) {
        if (origin != null)
            origin.value().powers().stream().map(Holder::value).filter(x -> x.isActive(this)).forEach(consumer);
    }

    //Ticking
    public void sync() {
        this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void tick(@NotNull Entity entity) {
        this.getOrigins().values().forEach(o -> this.executeOnPowers(o, p -> p.tick(entity)));
        //Check components and update
        if (this.data.getComponents().values().stream().flatMap(x -> x.values().stream()).map(PowerComponent::isDirty).reduce(false, (p, c) -> p | c))
            this.sync();
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        OriginDataHolder.get(event.getEntity()).tick(event.getEntity());
    }
}
