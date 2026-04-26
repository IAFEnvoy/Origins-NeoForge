package com.iafenvoy.origins.event;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnBeingUsedPower;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnEntityUsePower;
import com.iafenvoy.origins.util.MiscUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber
public final class CommonEvents {
    @SubscribeEvent
    public static void preventDamageWhenSelecting(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof Player player && !OriginDataHolder.get(player).hasAllOrigins())
            event.setInvulnerable(true);
    }

    //TODO::Move to power class
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        Entity target = event.getTarget();
        Optional<InteractionResult> result = ActionOnEntityUsePower.tryPrevent(player, target, event.getHand());
        if (result.isEmpty() || result.get() == InteractionResult.PASS) {
            result = ActionOnBeingUsedPower.tryPrevent(target, player, event.getHand());
        }
        result.ifPresent(res -> {
            if (res != InteractionResult.PASS) {
                event.setCancellationResult(res);
                event.setCanceled(true);
            }
        });
    }

    //TODO::Move to power class
    //If the interaction isn't canceled, let other mod interaction play, as this can cancel interactions.
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void playerEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        Entity target = event.getTarget();
        Optional<InteractionResult> result = Stream.concat(
                ActionOnEntityUsePower.tryInteract(player, target, event.getHand()).stream(),
                ActionOnBeingUsedPower.tryInteract(target, player, event.getHand()).stream()).reduce(MiscUtil::reduce);
        result.ifPresent(res -> {
            if (res != InteractionResult.PASS) {
                event.setCancellationResult(res);
                event.setCanceled(true);
            }
        });
    }
}
