package com.iafenvoy.origins.command;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.RLHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

//TODO::"remove" sub command, optimize suggestions
public final class PowerCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("power")
                .requires(source -> source.hasPermission(2))
                .then(argument("target", EntityArgument.player())
                        .then(literal("grant")
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .executes(PowerCommand::grantDefault)
                                        .then(argument("source", ResourceLocationArgument.id())
                                                .suggests(PowerCommand::suggestAllSources)
                                                .executes(PowerCommand::grantFromSource))))
                        .then(literal("revoke")
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .suggests(PowerCommand::suggestPowers)
                                        .executes(PowerCommand::revokeDefault)
                                        .then(argument("source", ResourceLocationArgument.id())
                                                .suggests(PowerCommand::suggestSources)
                                                .executes(PowerCommand::revokeFromSource))))
                        .then(literal("clear")
                                .executes(PowerCommand::clear))
                        .then(literal("has")
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .executes(PowerCommand::has)
                                        .then(argument("source", ResourceLocationArgument.id())
                                                .suggests(PowerCommand::suggestAllSources)
                                                .executes(PowerCommand::has))))
                        .then(literal("list")
                                .executes(PowerCommand::list))
                        .then(literal("revokeall")
                                .then(argument("source", ResourceLocationArgument.id())
                                        .suggests(PowerCommand::suggestAllSources)
                                        .executes(PowerCommand::revokeAll)))
                        .then(literal("sources")
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .executes(PowerCommand::sources)))
                ));
    }

    private static CompletableFuture<Suggestions> suggestPowers(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        Collection<Holder<Power>> sources = OriginDataHolder.get(player).data().getPowers().values();
        Stream<String> stream = sources.stream().map(Holder::unwrapKey).filter(Optional::isPresent).map(Optional::get).map(ResourceKey::location).map(ResourceLocation::toString);
        return SharedSuggestionProvider.suggest(stream, builder);
    }

    private static CompletableFuture<Suggestions> suggestAllSources(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        Set<ResourceLocation> sources = new HashSet<>(OriginDataHolder.get(player).data().getPowers().keySet());
        sources.add(OriginDataHolder.DEFAULT_SOURCE);
        return SharedSuggestionProvider.suggest(sources.stream().map(ResourceLocation::toString), builder);
    }

    private static CompletableFuture<Suggestions> suggestSources(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        Holder.Reference<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        Stream<String> stream = OriginDataHolder.get(player).data().getPowers().entries().stream().filter(x -> x.getValue().value() == power.value()).map(Map.Entry::getKey).map(ResourceLocation::toString);
        return SharedSuggestionProvider.suggest(stream, builder);
    }

    private static int grantDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return grant(context, OriginDataHolder.DEFAULT_SOURCE);
    }

    private static int grantFromSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return grant(context, ResourceLocationArgument.getId(context, "source"));
    }

    private static int grant(CommandContext<CommandSourceStack> context, ResourceLocation source) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);

        CommandSourceStack src = context.getSource();
        OriginDataHolder holder = OriginDataHolder.get(target);
        holder.grantPower(source, power);
        holder.sync();

        src.sendSuccess(() -> Component.translatable("commands.power.grant.success", target.getName(), RLHelper.string(power), source.toString()), true);
        return 1;
    }

    private static int revokeDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return revoke(context, OriginDataHolder.DEFAULT_SOURCE);
    }

    private static int revokeFromSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return revoke(context, ResourceLocationArgument.getId(context, "source"));
    }

    private static int revoke(CommandContext<CommandSourceStack> context, ResourceLocation source) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);

        CommandSourceStack src = context.getSource();
        OriginDataHolder holder = OriginDataHolder.get(target);
        if (!holder.hasPower(source, power)) {
            src.sendFailure(Component.translatable("commands.power.revoke.failure", target.getName(), RLHelper.string(power), source.toString()));
            return 0;
        }
        holder.revokePower(source, power);
        holder.sync();

        src.sendSuccess(() -> Component.translatable("commands.power.revoke.success", target.getName(), RLHelper.string(power), source.toString()), true);
        return 1;
    }

    private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        OriginDataHolder holder = OriginDataHolder.get(target);
        // collect entries to avoid concurrent modification
        List<Map.Entry<ResourceLocation, Holder<Power>>> entries = new ArrayList<>(holder.data().getPowers().entries());
        for (Map.Entry<ResourceLocation, Holder<Power>> e : entries) holder.revokePower(e.getKey(), e.getValue());
        holder.sync();
        context.getSource().sendSuccess(() -> Component.translatable("commands.power.clear.success", target.getName()), true);
        return entries.size();
    }

    private static int has(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        CommandSourceStack src = context.getSource();
        OriginDataHolder holder = OriginDataHolder.get(target);
        try {
            ResourceLocation source = ResourceLocationArgument.getId(context, "source");
            if (holder.hasPower(source, power))
                src.sendSuccess(() -> Component.translatable("commands.power.has.success.source", target.getName(), RLHelper.string(power), source.toString()), false);
            else
                src.sendFailure(Component.translatable("commands.power.has.failure.source", target.getName(), RLHelper.string(power), source.toString()));
        } catch (Exception e) {
            if (holder.hasPower(power))
                src.sendSuccess(() -> Component.translatable("commands.power.has.success", target.getName(), RLHelper.string(power)), false);
            else
                src.sendFailure(Component.translatable("commands.power.has.failure", target.getName(), RLHelper.string(power)));
        }
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        OriginDataHolder holder = OriginDataHolder.get(target);
        String list = holder.data().getPowers().entries().stream().map(e -> RLHelper.string(e.getValue()) + " (" + e.getKey().toString() + ")").reduce((a, b) -> a + ", " + b).orElse("(none)");
        context.getSource().sendSuccess(() -> Component.translatable("commands.power.list.result", target.getName(), list), false);
        return 1;
    }

    private static int revokeAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        ResourceLocation source = ResourceLocationArgument.getId(context, "source");
        OriginDataHolder holder = OriginDataHolder.get(target);
        holder.revokeAllPowers(source);
        holder.sync();
        context.getSource().sendSuccess(() -> Component.translatable("commands.power.revokeall.success", target.getName(), source.toString()), true);
        return 1;
    }

    private static int sources(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        OriginDataHolder holder = OriginDataHolder.get(target);
        String list = holder.data().getPowers().entries().stream().filter(e -> e.getValue().equals(power)).map(Map.Entry::getKey).map(ResourceLocation::toString).reduce((a, b) -> a + ", " + b).orElse("(none)");
        context.getSource().sendSuccess(() -> Component.translatable("commands.power.sources.result", target.getName(), RLHelper.string(power), list), false);
        return 1;
    }
}