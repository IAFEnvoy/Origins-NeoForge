package com.iafenvoy.origins.network;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class LoginHelper {
    @SubscribeEvent
    public static void onSyncDatapack(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) forEachPlayer(event.getPlayer(), false);
        else for (ServerPlayer player : event.getPlayerList().getPlayers())
            forEachPlayer(player, true);
    }

    private static void forEachPlayer(@NotNull ServerPlayer player, boolean joined) {
        EntityOriginAttachment component = EntityOriginAttachment.get(player);
        component.sync(player);
        if (component.hasAllOrigins(player.registryAccess())) {
            component.refreshPowerMap();
            return;
        }
        component.fillAutoChoosing(player);
        if (!component.hasAllOrigins(player.registryAccess()))
            if (!isFakePlayer(player)) {
                component.setSelecting(true);
                component.sync(player);
                PacketDistributor.sendToPlayer(player, new OpenChooseOriginScreenS2CPayload(true));
                return;
            }
        component.sync(player);
    }

    private static boolean isFakePlayer(ServerPlayer player) {
        return false;
        //TODO
//        return FabricLoader.getInstance().isModLoaded("carpet") && player instanceof EntityPlayerMPFake;
    }
}
