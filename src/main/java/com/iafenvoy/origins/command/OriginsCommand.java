package com.iafenvoy.origins.command;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.network.payload.OpenEditorS2CPayload;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.language.IModInfo;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class OriginsCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> registerCommand(CommandBuildContext context) {
        return literal(Origins.MOD_ID)
                .then(OriginCommand.registerCommand(context))
                .then(PowerCommand.registerCommand(context))
                .then(ResourceCommand.registerCommand())
                .then(literal("editor").executes(OriginsCommand::editor))
                .then(literal("version").executes(OriginsCommand::version));
    }

    private static int editor(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PacketDistributor.sendToPlayer(context.getSource().getPlayerOrException(), OpenEditorS2CPayload.INSTANCE);
        return 1;
    }

    private static int version(CommandContext<CommandSourceStack> context) {
        ModList.get().getModContainerById(Origins.MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).map(Object::toString).ifPresent(v -> context.getSource().sendSuccess(() -> Component.literal("v").append(v), false));
        return 1;
    }
}
