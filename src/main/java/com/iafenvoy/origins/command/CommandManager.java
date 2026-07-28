package com.iafenvoy.origins.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public final class CommandManager {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();

        dispatcher.register(OriginsCommand.registerCommand(context));
        dispatcher.register(OriginCommand.registerCommand(context));
        dispatcher.register(PowerCommand.registerCommand(context));
        dispatcher.register(ResourceCommand.registerCommand());
    }
}
