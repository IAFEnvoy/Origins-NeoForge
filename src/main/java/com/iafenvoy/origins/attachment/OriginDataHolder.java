package com.iafenvoy.origins.attachment;

import carpet.patches.EntityPlayerMPFake;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.Proxies;
import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.global_powers.GlobalPowersRegistries;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.event.GrantOriginEvent;
import com.iafenvoy.origins.event.GrantPowerEvent;
import com.iafenvoy.origins.event.RevokeOriginEvent;
import com.iafenvoy.origins.event.RevokePowerEvent;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.iafenvoy.origins.util.RLHelper;
import com.iafenvoy.origins.util.RandomHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber
public final class OriginDataHolder {
    public static final ResourceLocation DEFAULT_SOURCE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "command");
    public static final ResourceLocation GLOBAL = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "global");
    public static final ResourceLocation ITEM = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item");
    private final Entity entity;
    private final EntityOriginAttachment data;
    private final RegistryAccess access;
    private final PowerHelper helper;

    public OriginDataHolder(Entity entity, EntityOriginAttachment data) {
        this.entity = entity;
        this.data = data;
        this.access = entity.registryAccess();
        this.helper = new PowerHelper(this);
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityOriginAttachment getData() {
        return this.data;
    }

    public RegistryAccess getAccess() {
        return this.access;
    }

    public PowerHelper getHelper() {
        return this.helper;
    }

    //Query
    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return Map.copyOf(this.data.getOrigins());
    }

    public Holder<Origin> getOrigin(Holder<Layer> layer) {
        return this.data.getOrigins().get(layer);
    }

    //Origin Related
    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
        this.clearOrigin(layer);
        if (origin.value() == Origin.EMPTY) return;
        this.data.getOrigins().put(layer, origin);
        ResourceLocation id = RLHelper.id(origin);
        RegistryCodecs.listAll(origin.value().powers(), this.access, PowerRegistries.POWER_KEY).forEach(x -> this.grantPower(id, x));
        NeoForge.EVENT_BUS.post(new GrantOriginEvent(this.entity, layer, origin));
    }

    public void clearOrigin(@NotNull Holder<Layer> layer) {
        Holder<Origin> origin = this.data.getOrigins().remove(layer);
        if (origin == null) return;
        this.revokeAllPowers(RLHelper.id(origin));
        NeoForge.EVENT_BUS.post(new RevokeOriginEvent(this.entity, layer, origin));
    }

    public boolean hasOrigin(Holder<Layer> layer, Holder<Origin> origin) {
        return this.data.getOrigins().containsKey(layer) && this.data.getOrigins().get(layer).value().equals(origin.value());
    }

    public boolean hasOrigin(Holder<Origin> origin) {
        return this.data.getOrigins().containsValue(origin);
    }

    public boolean hasOriginInLayer(Holder<Layer> layer) {
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
        List<Holder<Origin>> available = layer.value().collectRandomizableOrigins(this.entity).toList();
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

    //Power Related
    public void grantPower(ResourceLocation source, Holder<Power> power) {
        this.data.getPowers().put(source, power);
        ComponentCollector collector = ComponentCollector.create();
        power.value().createComponents(collector);
        this.data.getComponents().put(RLHelper.id(power), collector.build());
        power.value().grant(this);
        NeoForge.EVENT_BUS.post(new GrantPowerEvent(this.entity, power, source));
        this.sync();
    }

    public void revokePower(ResourceLocation source, Holder<Power> power) {
        this.data.getPowers().remove(source, power);
        power.value().revoke(this);
        this.data.getComponents().remove(RLHelper.id(power));
        NeoForge.EVENT_BUS.post(new RevokePowerEvent(this.entity, power, source));
        this.sync();
    }

    public void revokeAllPowers(ResourceLocation source) {
        this.data.getPowers().entries().stream().filter(x -> x.getKey().equals(source)).map(Map.Entry::getValue).toList().forEach(p -> this.revokePower(source, p));
    }

    public void revokeAllPowers(Holder<Power> power) {
        this.data.getPowers().entries().stream().filter(x -> x.getValue().equals(power)).map(Map.Entry::getKey).forEach(s -> this.revokePower(s, power));
    }

    public Multimap<ResourceLocation, Holder<Power>> getPowers() {
        //TODO::Cache?
        ImmutableMultimap.Builder<ResourceLocation, Holder<Power>> builder = ImmutableMultimap.builder();
        builder.putAll(this.data.getPowers());
        //Global
        GlobalPowersRegistries.streamPowersForType(this.access, this.entity.getType()).forEach(h -> builder.put(GLOBAL, h));
        //Item
        if (this.entity instanceof LivingEntity living)
            for (EquipmentSlot slot : EquipmentSlot.values())
                living.getItemBySlot(slot).getOrDefault(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.EMPTY).powers().values().stream().map(ItemPowersComponent.Entry::power).forEach(h -> builder.put(ITEM, h));
        return builder.build();
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        List<T> results = this.getPowers().values().stream().filter(x -> RLHelper.id(x).equals(id)).map(Holder::value).toList().stream().filter(power -> power != null && clazz.isAssignableFrom(power.getClass())).map(clazz::cast).collect(Collectors.toCollection(LinkedList::new));
        return Prioritized.class.isAssignableFrom(clazz) ? results.stream().map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::getPriority)).map(clazz::cast).toList() : results;
    }

    //Only for toggle and hud render, which need to bypass active logic
    @NotNull
    public <T> Stream<T> streamPowers(Class<T> clazz) {
        Stream<T> results = this.getPowers().values().stream().map(Holder::value).filter(power -> clazz.isAssignableFrom(power.getClass())).map(clazz::cast);
        return Prioritized.class.isAssignableFrom(clazz) ? results.map(Prioritized.class::cast).sorted(Comparator.comparingInt(Prioritized::getPriority)).map(clazz::cast) : results;
    }

    @NotNull
    public <T extends Power> Stream<T> streamActivePowers(Class<T> clazz) {
        return this.streamPowers(clazz).filter(x -> x.isActive(this));
    }

    public boolean hasPower(Holder<Power> power) {
        return this.getPowers().values().stream().anyMatch(p -> p.equals(power));
    }

    public boolean hasPower(ResourceLocation source, Holder<Power> power) {
        return this.getPowers().entries().stream().anyMatch(e -> e.getKey().equals(source) && e.getValue().equals(power));
    }

    public <T extends Power> boolean hasActivePower(ResourceLocation id, Class<T> clazz) {
        return this.getPowers().values().stream().filter(x -> Objects.equals(RLHelper.id(x), id)).map(Holder::value).filter(x -> x.isActive(this)).anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
    }

    public <T extends Power> boolean hasActivePower(Class<T> clazz) {
        return this.getPowers().values().stream().map(Holder::value).filter(x -> x.isActive(this)).anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
    }

    //Component Related
    public <T> Optional<T> getComponent(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id).get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
        return this.getComponentFor(this.access.registryOrThrow(PowerRegistries.POWER_KEY).wrapAsHolder(power), clazz);
    }

    public <T> Optional<T> getComponentFor(Holder<Power> power, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(RLHelper.id(power))).map(x -> x.get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(ResourceLocation id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id)).map(x -> x.get(clazz)).filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast).map(x -> x.constructHolder(this));
    }

    //Utils
    public static OriginDataHolder get(Entity entity) {
        return new OriginDataHolder(entity, entity.getData(OriginsAttachments.ENTITY_ORIGIN));
    }

    //Ticking
    public void sync() {
        this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void tick() {
        long currentTick = Proxies.TICK_COUNT.getAsLong();
        this.getPowers().values().stream().map(Holder::value).filter(p -> p.tickInterval() <= 0 || currentTick % p.tickInterval() == 0).forEach(p -> p.tick(this));
        this.data.getComponents().forEach((id, map) -> map.values().forEach(c -> c.tick(this, id)));
        //Check components and update
        if (this.data.getComponents().values().stream().flatMap(x -> x.values().stream()).map(PowerComponent::isDirty).reduce(false, (p, c) -> p | c))
            this.sync();
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        OriginDataHolder.get(event.getEntity()).tick();
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        OriginDataHolder holder = OriginDataHolder.get(event.getEntity());
        holder.streamActivePowers(Power.class).forEach(x -> x.respawn(holder, event.isEndConquered()));
    }

    //FIXME::Should remove?
    @ApiStatus.Internal
    @SubscribeEvent
    public static void onSyncDatapack(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) forEachPlayer(event.getPlayer());
        else for (ServerPlayer player : event.getPlayerList().getPlayers()) forEachPlayer(player);
    }

    private static void forEachPlayer(@NotNull ServerPlayer player) {
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.sync();
        if (holder.hasAllOrigins()) return;
        holder.fillAutoChoosing();
        if (!holder.hasAllOrigins() && !isFakePlayer(player)) {
            holder.data.setSelecting(true);
            holder.sync();
            PacketDistributor.sendToPlayer(player, new OpenChooseOriginScreenS2CPayload(true));
            return;
        }
        holder.sync();
    }

    private static boolean isFakePlayer(ServerPlayer player) {
        return ModList.get().isLoaded("bedsheet") && player instanceof EntityPlayerMPFake;
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public static void onGrantAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        AdvancementHolder advancement = event.getAdvancement();
        OriginDataHolder holder = OriginDataHolder.get(player);
        Map<Holder<Layer>, Origin.Upgrade> upgrades = new LinkedHashMap<>();
        for (Map.Entry<Holder<Layer>, Holder<Origin>> origin : holder.getOrigins().entrySet())
            for (Origin.Upgrade x : origin.getValue().value().upgrades())
                if (Objects.equals(advancement.id(), x.condition())) {
                    upgrades.put(origin.getKey(), x);
                    break;
                }
        for (Map.Entry<Holder<Layer>, Origin.Upgrade> entry : upgrades.entrySet()) {
            Origin.Upgrade upgrade = entry.getValue();
            holder.setOrigin(entry.getKey(), upgrade.origin());
            upgrade.announcement().ifPresent(player::sendSystemMessage);
        }
    }
}
