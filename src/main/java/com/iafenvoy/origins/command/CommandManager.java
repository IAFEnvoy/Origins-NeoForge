package com.iafenvoy.origins.command;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public final class CommandManager {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        OriginCommand.registerCommand(event.getDispatcher(), event.getBuildContext());
        PowerCommand.registerCommand(event.getDispatcher(), event.getBuildContext());
    }
}
