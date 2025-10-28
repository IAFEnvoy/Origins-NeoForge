package com.iafenvoy.origins;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.network.payload.OpenChooseOriginScreenS2CPayload;
import com.iafenvoy.origins.util.RLHelper;
import com.iafenvoy.origins.util.RandomHelper;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber
public class OriginsCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        CommandBuildContext context = event.getBuildContext();
        event.getDispatcher().register(literal(Origins.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(literal("set")
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                        .then(argument("origin", ResourceArgument.resource(context, OriginRegistries.ORIGIN_KEY))
                                                .executes(OriginsCommand::set)))))
                .then(literal("get")
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                        .executes(OriginsCommand::get))))
                .then(literal("gui")
                        .executes(ctx -> OriginsCommand.openGuiAll(ctx, true))
                        .then(argument("targets", EntityArgument.players())
                                .executes(ctx -> OriginsCommand.openGuiAll(ctx, false))
                                .then(argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                        .executes(OriginsCommand::openGuiSpecific))))
                .then(literal("random")
                        .executes(ctx -> OriginsCommand.randomAll(ctx, true))
                        .then(argument("targets", EntityArgument.players())
                                .executes(ctx -> OriginsCommand.randomAll(ctx, false))
                                .then(argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                        .executes(OriginsCommand::randomSpecific))))
        );
    }

    public static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<ServerPlayer> targets = new ObjectArrayList<>(EntityArgument.getPlayers(context, "targets"));
        Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
        Holder<Origin> origin = ResourceArgument.getResource(context, "origin", OriginRegistries.ORIGIN_KEY);

        CommandSourceStack source = context.getSource();
        int processedTargets = 0;

        if (origin.value().equals(Origin.EMPTY) || origin.is(layer.value().origins())) {
            for (ServerPlayer target : targets) {
                EntityOriginAttachment originComponent = EntityOriginAttachment.get(target);
                originComponent.setOrigin(layer, origin, target);
                originComponent.sync(target);
                processedTargets++;
            }
            if (processedTargets == 1)
                source.sendSuccess(() -> Component.translatable("commands.origin.set.success.single", targets.getFirst().getName(), Layer.getName(layer), Origin.getName(origin)), true);
            else {
                int finalProcessedTargets = processedTargets;
                source.sendSuccess(() -> Component.translatable("commands.origin.set.success.multiple", finalProcessedTargets, Layer.getName(layer), Origin.getName(origin)), true);
            }
        } else
            source.sendFailure(Component.translatableEscape("commands.origin.unregistered_in_layer", id(origin), id(layer)));
        return processedTargets;
    }

    public static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);

        CommandSourceStack source = context.getSource();
        EntityOriginAttachment originComponent = EntityOriginAttachment.get(target);

        Holder<Origin> origin = originComponent.getOrigin(layer);
        source.sendSuccess(() -> Component.translatable("commands.origin.get.result", target.getName(), Layer.getName(layer), Origin.getName(origin), id(origin)), false);

        return 1;
    }

    public static int openGuiAll(CommandContext<CommandSourceStack> context, boolean self) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Collection<ServerPlayer> targets = self ? List.of(source.getPlayerOrException()) : EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer target : targets) openGuiForLayer(target, null);
        source.sendSuccess(() -> Component.translatable("commands.origin.gui.all", targets.size()), true);
        return targets.size();
    }

    public static int openGuiSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
        for (ServerPlayer target : targets) openGuiForLayer(target, layer);
        context.getSource().sendSuccess(() -> Component.translatable("commands.origin.gui.layer", targets.size(), Layer.getName(layer)), true);
        return targets.size();

    }

    public static int randomAll(CommandContext<CommandSourceStack> context, boolean self) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Collection<ServerPlayer> targets = self ? List.of(source.getPlayerOrException()) : EntityArgument.getPlayers(context, "targets");
        List<Holder<Layer>> layers = LayerRegistries.streamRandomizableLayers(context.getSource().registryAccess()).toList();

        for (ServerPlayer target : targets)
            for (Holder<Layer> layer : layers)
                setAndGetRandomOrigin(target, layer);

        source.sendSuccess(() -> Component.translatable("commands.origin.random.all", targets.size(), layers.size()), true);
        return targets.size();

    }

    public static int randomSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<ServerPlayer> targets = new ObjectArrayList<>(EntityArgument.getPlayers(context, "targets"));
        Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);

        CommandSourceStack source = context.getSource();
        AtomicReference<Holder<Origin>> origin = new AtomicReference<>();

        if (layer.value().allowRandom()) {
            for (ServerPlayer target : targets) origin.set(setAndGetRandomOrigin(target, layer));
            if (targets.size() > 1)
                source.sendSuccess(() -> Component.translatable("commands.origin.random.success.multiple", targets.size(), Layer.getName(layer)), true);
            else
                source.sendSuccess(() -> Component.translatable("commands.origin.random.success.single", targets.getFirst().getName(), Origin.getName(origin.get()), Layer.getName(layer)), true);
        }

        return targets.size();
    }

    private static Holder<Origin> setAndGetRandomOrigin(ServerPlayer target, Holder<Layer> layer) {
        List<Holder<Origin>> origins = layer.value().collectRandomizableOrigins(target.registryAccess()).toList();
        EntityOriginAttachment originComponent = EntityOriginAttachment.get(target);
        Holder<Origin> origin = RandomHelper.randomOne(origins);
        originComponent.setOrigin(layer, origin, target);
        originComponent.fillAutoChoosing(target);
        originComponent.sync(target);
        Origins.LOGGER.info("Player {} was randomly assigned the origin {} for layer {}", target.getName().getString(), RLHelper.id(origin), RLHelper.id(layer));
        return origin;
    }

    private static void openGuiForLayer(ServerPlayer target, @Nullable Holder<Layer> targetLayer) {
        EntityOriginAttachment originComponent = EntityOriginAttachment.get(target);
        List<Holder<Layer>> layers = new ObjectArrayList<>();

        Optional.ofNullable(targetLayer).ifPresentOrElse(layers::add, () -> layers.addAll(LayerRegistries.streamAvailableLayers(target.registryAccess()).toList()));

        layers.stream()
                .filter(x -> x.value().enabled())
                .forEach(layer -> originComponent.clearOrigin(layer, target));

        boolean automaticallyAssigned = originComponent.fillAutoChoosing(target);
        int options = Optional.ofNullable(targetLayer)
                .map(layer -> layer.value().getOriginOptionCount(target.registryAccess()))
                .orElseGet(() -> OriginRegistries.streamAvailableOrigins(target.registryAccess()).toList().size());

        originComponent.setSelecting(!automaticallyAssigned || options > 0);
        originComponent.sync(target);

        if (originComponent.isSelecting())
            PacketDistributor.sendToPlayer(target, new OpenChooseOriginScreenS2CPayload(false));
    }

    public static String id(Holder<?> holder) {
        return RLHelper.string(holder);
    }
}
