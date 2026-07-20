package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.*;
import com.iafenvoy.origins.registry.OriginsKeyMappings;
import com.iafenvoy.origins.render.LevelRenderHelper;
import com.iafenvoy.origins.screen.ChooseOriginScreen;
import com.iafenvoy.origins.screen.WaitForNextLayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class ClientNetworkHandler {
    static void onOriginConfirm(ConfirmOriginS2CPayload packet, IPayloadContext context) {
        Player player = context.player();
        OriginDataHolder holder = OriginDataHolder.get(context.player());
        holder.setOrigin(packet.layer(), packet.origin());
        player.sendSystemMessage(Component.translatable("commands.origin.set.success.single", player.getDisplayName(), Layer.getName(packet.layer()), Origin.getName(packet.origin())));
        if (Minecraft.getInstance().screen instanceof WaitForNextLayerScreen nextLayerScreen)
            nextLayerScreen.openSelection();
    }

    static void openOriginScreen(OpenChooseOriginScreenS2CPayload packet, IPayloadContext context) {
        OriginDataHolder holder = OriginDataHolder.get(context.player());
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(context.player().registryAccess())
                .filter(x -> !holder.hasOriginInLayer(x) && x.value().getOriginOptionCount(context.player()) > 0)
                .sorted(Comparator.comparing(Holder::value))
                .toList();
        ClientCall.openOriginScreen(layers, packet.showBackground());
    }

    public static void onReapplyShaders(ReapplyShadersS2CPayload payload, IPayloadContext context) {
        ClientCall.onReapplyShaders();
    }

    public static void onReloadLevelRenderer(ReloadLevelRendererS2CPayload payload, IPayloadContext context) {
        LevelRenderHelper.reload();
    }

    public static void onNotifyKeymaps(NotifyKeymapsS2CPayload payload, IPayloadContext context) {
        OriginsKeyMappings.INSTANCE.registerKeyMappingsFromPowers(OriginDataHolder.optional(context.player()).map(OriginDataHolder::getAllPowers).orElse(Set.of()));
    }

    public static void onMountPlayer(MountPlayerS2CPayload payload, IPayloadContext context) {
        Entity source = context.player().level().getEntity(payload.source());
        Entity target = context.player().level().getEntity(payload.target());

        if (source == null || target == null) {
            Origins.LOGGER.warn("Received MountPlayerS2CPayload with invalid entity IDs: source={}, target={}", payload.source(), payload.target());
            return;
        }

        source.startRiding(target);
    }

    public static void onDismountPlayer(DismountPlayerS2CPayload payload, IPayloadContext context) {
        Entity dismounter = context.player().level().getEntity(payload.dismountingEntity());

        if (dismounter == null) {
            Origins.LOGGER.warn("Received DismountPlayerS2CPayload with invalid entity ID: dismountingEntity={}", payload.dismountingEntity());
            return;
        }

        dismounter.stopRiding();
    }

    //If I don't call in a single class server will crash
    private static final class ClientCall {
        public static void openOriginScreen(List<Holder<Layer>> layers, boolean showBackground) {
            if (layers.isEmpty()) {
                Minecraft.getInstance().setScreen(null);
                return;
            }
            Minecraft.getInstance().setScreen(new ChooseOriginScreen(layers, 0, showBackground));
        }

        public static void onReapplyShaders() {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gameRenderer.checkEntityPostEffect(minecraft.options.getCameraType().isFirstPerson() ? minecraft.getCameraEntity() : null);
        }
    }


}
