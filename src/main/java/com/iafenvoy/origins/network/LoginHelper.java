package com.iafenvoy.origins.network;

import carpet.patches.EntityPlayerMPFake;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public final class LoginHelper {
    //FIXME::Merge with refreshing power maps
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
        return ModList.get().isLoaded("bedsheet") && player instanceof EntityPlayerMPFake;
    }

    public static void openGuiForLayer(ServerPlayer target, @Nullable Holder<Layer> layer) {
        EntityOriginAttachment attachment = EntityOriginAttachment.get(target);
        List<Holder<Layer>> layers = new ObjectArrayList<>();

        Optional.ofNullable(layer).ifPresentOrElse(layers::add, () -> layers.addAll(LayerRegistries.streamAvailableLayers(target.registryAccess()).toList()));

        layers.stream()
                .filter(x -> x.value().enabled())
                .forEach(l -> attachment.clearOrigin(l, target));

        boolean automaticallyAssigned = attachment.fillAutoChoosing(target);
        int options = Optional.ofNullable(layer)
                .map(l -> l.value().getOriginOptionCount(target.registryAccess()))
                .orElseGet(() -> OriginRegistries.streamAvailableOrigins(target.registryAccess()).toList().size());

        attachment.setSelecting(!automaticallyAssigned || options > 0);
        attachment.sync(target);

        if (attachment.isSelecting())
            PacketDistributor.sendToPlayer(target, new OpenChooseOriginScreenS2CPayload(false));
    }
}
