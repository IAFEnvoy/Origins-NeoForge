package com.iafenvoy.origins.attachment;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.Proxies;
import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.Sided;
import com.iafenvoy.origins.data.global_powers.GlobalPowersRegistries;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.MultiplePower;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.event.GrantOriginEvent;
import com.iafenvoy.origins.event.GrantPowerEvent;
import com.iafenvoy.origins.event.RevokeOriginEvent;
import com.iafenvoy.origins.event.RevokePowerEvent;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsKeyMappings;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.iafenvoy.origins.util.HolderHelper;
import com.iafenvoy.origins.util.RandomHelper;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@EventBusSubscriber
public final class OriginDataHolder {
    public static final Identifier DEFAULT_SOURCE = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "command");
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

    // 查询
    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return Map.copyOf(this.data.getOrigins());
    }

    public Holder<Origin> getOrigin(Holder<Layer> layer) {
        return this.data.getOrigins().get(layer);
    }

    // 起源相关
    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
        this.clearOrigin(layer);
        if (origin.value() == Origin.EMPTY)
            return;
        this.data.getOrigins().put(layer, origin);
        Identifier id = HolderHelper.id(origin);
        RegistryCodecs.listAll(origin.value().powers(), this.access, PowerRegistries.POWER_KEY)
                .forEach(x -> this.grantPower(id, x));
        NeoForge.EVENT_BUS.post(new GrantOriginEvent(this.entity, layer, origin));
    }

    public void clearOrigin(@NotNull Holder<Layer> layer) {
        Holder<Origin> origin = this.data.getOrigins().remove(layer);
        if (origin == null)
            return;
        this.revokeAllPowers(HolderHelper.id(origin));
        NeoForge.EVENT_BUS.post(new RevokeOriginEvent(this.entity, layer, origin));
    }

    public boolean hasOrigin(Holder<Layer> layer, Holder<Origin> origin) {
        return this.data.getOrigins().containsKey(layer)
                && this.data.getOrigins().get(layer).value().equals(origin.value());
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
            if (this.data.getOrigins().containsKey(layer))
                continue;
            changed |= this.randomOrigin(layer);
        }
        if (changed)
            this.sync();
        return changed;
    }

    public boolean randomOrigin(Holder<Layer> layer) {
        List<Holder<Origin>> available = layer.value().collectRandomizableOrigins(this.entity).toList();
        if (!available.isEmpty()) {
            @NotNull
            Holder<Origin> origin = RandomHelper.randomOne(available);
            this.clearOrigin(layer);
            if (origin.value() != Origin.EMPTY) {
                if (this.entity.level().isClientSide())
                    if (this.entity instanceof net.minecraft.world.entity.player.Player pl)
                        pl.sendSystemMessage(Component.translatable("commands.origin.set.success.single",
                                this.entity.getDisplayName(), Layer.getName(layer), Origin.getName(origin)));
                this.setOrigin(layer, origin);
            }
            return true;
        }
        return false;
    }

    public boolean hasAllOrigins() {
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(this.access).toList();
        for (Holder<Layer> layer : layers) {
            if (this.data.getOrigins().containsKey(layer))
                continue;
            return false;
        }
        return true;
    }

    // 能力相关
    public void grantPower(Identifier source, Holder<Power> power) {
        this.data.getPowers().put(source, power);
        this.grantPower(new PowerHolder(power));
        NeoForge.EVENT_BUS.post(new GrantPowerEvent(this.entity, power, source));
        this.sync();
    }

    private void grantPower(PowerHolder power) {
        ComponentCollector collector = ComponentCollector.create();
        power.power().createComponents(collector);
        this.data.getComponents().put(power.id(), collector.build());
        power.power().grant(this);
        if (power.power() instanceof MultiplePower multiple)
            multiple.getPowers(power.id()).forEach(this::grantPower);
        if (this.getEntity().level().isClientSide())
            ClientCalls.registerKeyMappings(this.getAllPowers());
        // 26.1 移植说明：客户端按键映射注册在客户端层离线期间被禁用。
        // if (this.getEntity().level().isClientSide())
        // OriginsKeyMappings.INSTANCE.registerKeyMappingsFromPowers(this.getAllPowers());
    }

    private static final class ClientCalls {
        private static void registerKeyMappings(Set<PowerHolder> powers) {
            OriginsKeyMappings.INSTANCE.registerKeyMappingsFromPowers(powers);
        }
    }

    public void revokePower(Identifier source, Holder<Power> power) {
        this.data.getPowers().remove(source, power);
        this.revokePower(new PowerHolder(power));
        NeoForge.EVENT_BUS.post(new RevokePowerEvent(this.entity, power, source));
        this.sync();
    }

    private void revokePower(PowerHolder power) {
        power.power().revoke(this);
        this.data.getComponents().remove(power.id());
        if (power.power() instanceof MultiplePower multiple)
            multiple.getPowers(power.id()).forEach(this::revokePower);
    }

    public void revokeAllPowers(Identifier source) {
        this.data.getPowers().entries().stream().filter(x -> x.getKey().equals(source)).map(Map.Entry::getValue)
                .toList().forEach(p -> this.revokePower(source, p));
    }

    public void revokeAllPowers(Holder<Power> power) {
        this.data.getPowers().entries().stream().filter(x -> x.getValue().equals(power)).map(Map.Entry::getKey)
                .forEach(s -> this.revokePower(s, power));
    }

    public Multimap<Identifier, Holder<Power>> getEntityPowers() {
        return this.data.getPowers();
    }

    public Set<PowerHolder> getAllPowers() {
        // 待办::是否需要缓存？
        ImmutableSet.Builder<PowerHolder> builder = ImmutableSet.builder();
        for (Holder<Power> power : this.data.getPowers().values())
            if (power.value() instanceof MultiplePower multiple)
                multiple.getPowers(HolderHelper.id(power)).forEach(builder::add);
            else
                builder.add(new PowerHolder(power));
        // 全局
        GlobalPowersRegistries.streamPowersForType(this.access, this.entity.getType()).map(PowerHolder::new)
                .forEach(builder::add);
        // 物品
        if (this.entity instanceof LivingEntity living)
            for (EquipmentSlot slot : EquipmentSlot.values())
                living.getItemBySlot(slot).getOrDefault(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.EMPTY)
                        .powers().values().stream().map(ItemPowersComponent.Entry::power).map(PowerHolder::new)
                        .forEach(builder::add);
        return builder.build();
    }

    // 仅用于切换和HUD渲染，这些需要绕过激活逻辑
    private <T> Stream<T> streamPowers(Class<T> clazz, Predicate<Identifier> idChecker) {
        Stream<Power> powers = this.getAllPowers().stream().filter(x -> idChecker.test(x.id())).map(PowerHolder::power)
                .filter(power -> clazz.isAssignableFrom(power.getClass()));
        if (this.entity.level().isClientSide()) {
            powers = powers.filter(power -> {
                if (power.getSettings().condition() instanceof Sided condition) {
                    return !condition.server();
                } else
                    return true;
            });
        }

        Stream<T> results = powers.map(clazz::cast);

        return Prioritized.class.isAssignableFrom(clazz) ? results.map(Prioritized.class::cast)
                .sorted(Comparator.comparingInt(Prioritized::getPriority)).map(clazz::cast) : results;
    }

    public <T> Stream<T> streamPowers(Class<T> clazz) {
        return this.streamPowers(clazz, id -> true);
    }

    public <T extends Power> Stream<T> streamPowers(Identifier id, Class<T> clazz) {
        return this.streamPowers(clazz, i -> Objects.equals(i, id));
    }

    public <T extends Power> Stream<T> streamActivePowers(Class<T> clazz) {
        return this.streamPowers(clazz).filter(x -> x.isActive(this));
    }

    public boolean hasPower(Holder<Power> power) {
        return this.getAllPowers().stream().anyMatch(p -> Objects.equals(p, new PowerHolder(power)));
    }

    public boolean hasPower(Identifier id, Class<Power> clazz) {
        return this.streamPowers(id, clazz).findAny().isPresent();
    }

    public <T extends Power> boolean hasActivePower(Class<T> clazz) {
        return this.streamPowers(clazz).filter(x -> x.isActive(this))
                .anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
    }

    public <T extends Power> boolean hasActivePower(Identifier id, Class<T> clazz) {
        return this.streamPowers(id, clazz).filter(x -> x.isActive(this))
                .anyMatch(p -> clazz.isAssignableFrom(p.getClass()));
    }

    // 组件相关
    public <T> Optional<T> getComponent(Identifier id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id)).map(x -> x.get(clazz))
                .filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast);
    }

    public <T> Optional<T> getComponentFor(Power power, Class<T> clazz) {
        return this.getComponent(power.getId(this.access), clazz);
    }

    public <H, T extends ComponentHolderProvider<H>> Optional<H> getComponentHolder(Identifier id, Class<T> clazz) {
        return Optional.ofNullable(this.data.getComponents().get(id)).map(x -> x.get(clazz))
                .filter(x -> clazz.isAssignableFrom(x.getClass())).map(clazz::cast).map(x -> x.constructHolder(this));
    }

    // 工具方法
    public static OriginDataHolder get(@NotNull Entity entity) {
        return new OriginDataHolder(entity, entity.getData(OriginsAttachments.ENTITY_ORIGIN));
    }

    // Tick更新
    public void sync() {
        this.entity.syncData(OriginsAttachments.ENTITY_ORIGIN);
    }

    public void tick() {
        long currentTick = Proxies.TICK_COUNT.getAsLong();
        Set<PowerHolder> powers = this.getAllPowers();
        powers.stream().map(PowerHolder::power)
                .filter(p -> p.tickInterval() <= 0 || currentTick % p.tickInterval() == 0).forEach(p -> p.tick(this));
        // 更新组件
        boolean changed = false;
        for (PowerHolder power : powers)
            for (Map.Entry<Class<? extends PowerComponent>, PowerComponent> entry : this.data.getComponents()
                    .getOrDefault(power.id(), new LinkedHashMap<>()).entrySet()) {
                PowerComponent component = entry.getValue();
                component.tick(this, power);
                if (component.isDirty())
                    changed = true;
            }
        if (changed)
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

    // 修复::是否应该删除？
    @ApiStatus.Internal
    @SubscribeEvent
    public static void onSyncDatapack(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null)
            forEachPlayer(event.getPlayer());
        else
            for (ServerPlayer player : event.getPlayerList().getPlayers())
                forEachPlayer(player);
    }

    private static void forEachPlayer(@NotNull ServerPlayer player) {
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.sync();
        if (holder.hasAllOrigins())
            return;
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
        if (player instanceof FakePlayer)
            return true;
        // Carpet/bedsheet 还没有 Minecraft 26.1 版本，因此通过 EntityPlayerMPFake
        // 进行假人检测的功能暂时被禁用。
        return false;
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
