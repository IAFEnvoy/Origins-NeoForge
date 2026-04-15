package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
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
    public void grantPower(Holder<Power> power) {
        this.grantPower(DEFAULT_SOURCE, power);
    }

    public void grantPower(ResourceLocation source, Holder<Power> power) {
        this.data.getPowers().put(source, power);
        this.data.getComponents().put(RLHelper.id(power), power.value().createComponents().stream().collect(Collectors.toMap(PowerComponent::getClass, Function.identity())));
        power.value().grant(this.entity);
    }

    public void revokePower(Holder<Power> power) {
        this.revokePower(DEFAULT_SOURCE, power);
    }

    public void revokePower(ResourceLocation source, Holder<Power> power) {
        power.value().revoke(this.entity);
        this.data.getComponents().remove(RLHelper.id(power));
        this.data.getPowers().remove(source, power);
    }

    public void revokeAllPowers(ResourceLocation source) {
        this.data.getPowers().entries().stream().filter(x -> x.getKey().equals(source)).map(Map.Entry::getValue).toList().forEach(p -> this.revokePower(source, p));
    }

    public void revokeAllPowers(Holder<Power> power) {
        this.data.getPowers().values().remove(power);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(DeferredHolder<MapCodec<? extends Power>, MapCodec<T>> holder, Class<T> clazz) {
        return this.getPowers(holder.getId(), clazz);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        List<T> results = this.data.getPowers().values().stream().filter(x -> x.unwrapKey().map(ResourceKey::location).map(id::equals).orElse(false)).map(Holder::value).toList().stream().filter(power -> power != null && clazz.isAssignableFrom(power.getClass())).map(clazz::cast).collect(Collectors.toCollection(LinkedList::new));
        return Prioritized.class.isAssignableFrom(clazz) ? results.stream().map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast).toList() : results;
    }

    @NotNull
    public <T extends Power> Stream<T> streamPowers(Class<T> clazz) {
        Stream<T> results = this.data.getPowers().values().stream().map(Holder::value).filter(power -> clazz.isAssignableFrom(power.getClass())).map(clazz::cast);
        return Prioritized.class.isAssignableFrom(clazz) ? results.map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::priority)).map(clazz::cast) : results;
    }

    public void onPowerToggle(int index) {
        this.streamPowers(Toggleable.class).forEach(x -> x.toggle(this, index));
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
        origin.value().powers().forEach(x -> this.revokePower(id, x));
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

    private static void executeOnPowers(@Nullable Holder<Origin> origin, Consumer<Power> consumer) {
        if (origin != null) origin.value().powers().stream().map(Holder::value).forEach(consumer);
    }

    //Ticking
    public void sync() {
        this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void tick(@NotNull Entity entity) {
        this.getOrigins().values().forEach(o -> executeOnPowers(o, p -> p.tick(entity)));
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        OriginDataHolder.get(event.getEntity()).tick(event.getEntity());
    }
}
