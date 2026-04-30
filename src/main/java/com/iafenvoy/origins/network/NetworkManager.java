package com.iafenvoy.origins.network;

import com.iafenvoy.origins.network.payload.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber
public final class NetworkManager {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(ChooseOriginC2SPayload.TYPE, ChooseOriginC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ServerNetworkHandler::onChooseOrigin))
                .playToServer(ChooseRandomOriginC2SPayload.TYPE, ChooseRandomOriginC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ServerNetworkHandler::onChooseRandomOrigin))
                .playToClient(ConfirmOriginS2CPayload.TYPE, ConfirmOriginS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onOriginConfirm))
                .playToClient(OpenChooseOriginScreenS2CPayload.TYPE, OpenChooseOriginScreenS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::openOriginScreen))
                .playToServer(PowerToggleC2SPayload.TYPE, PowerToggleC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ServerNetworkHandler::onPowerToggle))
                .playToClient(ReapplyShadersS2CPayload.TYPE, ReapplyShadersS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onReapplyShaders))
                .playToClient(ReloadLevelRendererS2CPayload.TYPE, ReloadLevelRendererS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onReloadLevelRenderer));
    }
}
