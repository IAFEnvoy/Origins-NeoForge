package com.iafenvoy.origins.event.handler;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnBeingUsedPower;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnEntityUsePower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventBeingUsedPower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventEntityUsePower;
import com.iafenvoy.origins.network.payload.DismountPlayerS2CPayload;
import com.iafenvoy.origins.network.payload.NotifyKeymapsS2CPayload;
import com.iafenvoy.origins.util.MiscUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber
public final class CommonEvents {
    @ApiStatus.Internal
    @SubscribeEvent
    public static void clearOriginHolderCache(ServerTickEvent.Post event) {
        OriginDataHolder.clearCache();
    }

    @SubscribeEvent
    public static void preventDamageWhenSelecting(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof Player player && OriginDataHolder.optionalStream(player).noneMatch(OriginDataHolder::hasAllOrigins))
            event.setInvulnerable(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        Entity target = event.getTarget();
        Optional<InteractionResult> result = PreventEntityUsePower.tryPrevent(player, target, event.getHand());
        if (result.isEmpty() || result.get() == InteractionResult.PASS)
            result = PreventBeingUsedPower.tryPrevent(target, player, event.getHand());
        result.ifPresent(res -> {
            if (res != InteractionResult.PASS) {
                event.setCancellationResult(res);
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof ServerPlayer)
            PacketDistributor.sendToAllPlayers(new DismountPlayerS2CPayload(event.getEntityMounting().getId()));
    }


    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            PacketDistributor.sendToPlayer(player, NotifyKeymapsS2CPayload.INSTANCE);
    }

    @SubscribeEvent()
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        //fixes bug if player logs out while riding another player. Vanilla tries to take that entity with them by default, causing desync upon relog.
        if (event.getEntity().getRootVehicle() instanceof ServerPlayer)
            event.getEntity().stopRiding();
    }

    //If the interaction isn't canceled, let other mod interaction play, as this can cancel interactions.
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        Entity target = event.getTarget();
        InteractionHand hand = event.getHand();
        Stream.concat(
                PowerHelper.get(player).streamActive(ActionOnEntityUsePower.class).flatMap(p -> p.tryExecute(player, target, hand).stream()),
                PowerHelper.get(target).streamActive(ActionOnBeingUsedPower.class).flatMap(p -> p.tryExecute(target, player, hand).stream())
        ).reduce(MiscUtil::reduce).filter(res -> res != InteractionResult.PASS).ifPresent(res -> {
            event.setCancellationResult(res);
            event.setCanceled(true);
        });
    }
}
