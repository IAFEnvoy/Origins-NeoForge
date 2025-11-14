package com.iafenvoy.origins.network;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.network.payload.ConfirmOriginS2CPayload;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.screen.ChooseOriginScreen;
import com.iafenvoy.origins.screen.WaitForNextLayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Comparator;
import java.util.List;

public final class ClientNetworkHandler {
    static void onOriginConfirm(ConfirmOriginS2CPayload packet, IPayloadContext context) {
        Player player = context.player();
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.setOrigin(packet.layer(), packet.origin());
        if (Minecraft.getInstance().screen instanceof WaitForNextLayerScreen nextLayerScreen)
            nextLayerScreen.openSelection();
    }

    static void openOriginScreen(OpenChooseOriginScreenS2CPayload packet, IPayloadContext context) {
        OriginDataHolder holder = OriginDataHolder.get(context.player());
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(context.player().registryAccess()).filter(x -> !holder.hasOrigin(x)).sorted(Comparator.comparing(Holder::value)).toList();
        ClientCall.openOriginScreen(layers, packet.showBackground());
    }

    //If I don't call in a single class server will crash
    private static final class ClientCall {
        public static void openOriginScreen(List<Holder<Layer>> layers, boolean showBackground) {
            Minecraft.getInstance().setScreen(new ChooseOriginScreen(layers, 0, showBackground));
        }
    }
}
