package com.iafenvoy.origins.event.handler;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber
public final class CommonEvents {
    @SubscribeEvent
    public static void preventDamageWhenSelecting(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof Player player && !OriginDataHolder.get(player).hasAllOrigins())
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
        //修复玩家在骑乘另一玩家时登出的 Bug。原版默认会尝试将该实体一起带走，导致重新登录时数据不同步。
        if (event.getEntity().getRootVehicle() instanceof ServerPlayer)
            event.getEntity().stopRiding();
    }

    //如果交互没有被取消，让其他模组的交互执行，因为此操作可能会取消交互。
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        Entity target = event.getTarget();
        InteractionHand hand = event.getHand();
        Stream.concat(
                OriginDataHolder.get(player).streamActivePowers(ActionOnEntityUsePower.class).flatMap(p -> p.tryExecute(player, target, hand).stream()),
                OriginDataHolder.get(target).streamActivePowers(ActionOnBeingUsedPower.class).flatMap(p -> p.tryExecute(target, player, hand).stream())
        ).reduce(MiscUtil::reduce).filter(res -> res != InteractionResult.PASS).ifPresent(res -> {
            event.setCancellationResult(res);
            event.setCanceled(true);
        });
    }
}
