package com.iafenvoy.origins.network;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
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
    public static void onOriginConfirm(ConfirmOriginS2CPayload packet, IPayloadContext context) {
        Player player = context.player();
        EntityOriginAttachment component = EntityOriginAttachment.get(player);
        component.setOrigin(packet.layer(), packet.origin(), player);
        if (Minecraft.getInstance().screen instanceof WaitForNextLayerScreen nextLayerScreen)
            nextLayerScreen.openSelection();
    }

    public static void openOriginScreen(OpenChooseOriginScreenS2CPayload packet, IPayloadContext context) {
        EntityOriginAttachment component = EntityOriginAttachment.get(context.player());
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(context.player().registryAccess()).filter(x -> !component.hasOrigin(x)).sorted(Comparator.comparing(Holder::value)).toList();
        Minecraft.getInstance().setScreen(new ChooseOriginScreen(layers, 0, packet.showBackground()));
    }
}
