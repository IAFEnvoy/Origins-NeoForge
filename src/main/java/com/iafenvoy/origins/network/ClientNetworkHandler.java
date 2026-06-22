package com.iafenvoy.origins.network;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.*;
import com.iafenvoy.origins.registry.OriginsKeyMappings;
import com.iafenvoy.origins.registry.OriginsRenderers;
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

public final class ClientNetworkHandler {
    static void onOriginConfirm(ConfirmOriginS2CPayload packet, IPayloadContext context) {
        Player player = context.player();
        OriginDataHolder holder = OriginDataHolder.get(player);
        holder.setOrigin(packet.layer(), packet.origin());
        player.sendSystemMessage(Component.translatable("commands.origin.set.success.single", player.getDisplayName(), Layer.getName(packet.layer()), Origin.getName(packet.origin())));
        if (Minecraft.getInstance().screen instanceof WaitForNextLayerScreen nextLayerScreen) nextLayerScreen.openSelection();
    }

    static void openOriginScreen(OpenChooseOriginScreenS2CPayload packet, IPayloadContext context) {
        OriginDataHolder holder = OriginDataHolder.get(context.player());
        List<Holder<Layer>> layers = LayerRegistries.streamAvailableLayers(context.player().registryAccess())
                .filter(layer -> !holder.hasOriginInLayer(layer))
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
        OriginsKeyMappings.INSTANCE.registerKeyMappingsFromPowers(OriginDataHolder.get(context.player()).getAllPowers());
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

    public static void onRecipeBadge(RecipeBadgeS2CPayload payload, IPayloadContext context) {
        OriginsRenderers.receiveRecipeBadge(payload);
    }

    private static final class ClientCall {
        private static void openOriginScreen(List<Holder<Layer>> layers, boolean showBackground) {
            Minecraft.getInstance().setScreen(new ChooseOriginScreen(layers, 0, showBackground));
        }

        private static void onReapplyShaders() {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gameRenderer.checkEntityPostEffect(
                    minecraft.options.getCameraType().isFirstPerson() ? minecraft.getCameraEntity() : null
            );
        }
    }
}
