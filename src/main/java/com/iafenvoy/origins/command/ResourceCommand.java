package com.iafenvoy.origins.command;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.util.RLHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ResourceCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("resource")
                .requires(source -> source.hasPermission(2))
                .then(literal("has")
                        .then(argument("target", EntityArgument.entity())
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .executes(ResourceCommand::has))))
                .then(literal("get")
                        .then(argument("target", EntityArgument.entity())
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .executes(ResourceCommand::get))))
                .then(literal("change")
                        .then(argument("target", EntityArgument.entity())
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .then(argument("value", IntegerArgumentType.integer())
                                                .executes(ResourceCommand::change)))))
                .then(literal("operation")
                        .then(argument("target", EntityArgument.entity())
                                .then(argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                        .then(operationBranch("%=", Operation.MOD))
                                        .then(operationBranch("*=", Operation.MUL))
                                        .then(operationBranch("+=", Operation.ADD))
                                        .then(operationBranch("-=", Operation.SUB))
                                        .then(operationBranch("/=", Operation.DIV))
                                        .then(operationBranch("<", Operation.MIN))
                                        .then(operationBranch("=", Operation.SET))
                                        .then(operationBranch(">", Operation.MAX))
                                        .then(operationBranch("><", Operation.SWAP))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> operationBranch(String symbol, Operation operation) {
        return literal(symbol)
                .then(argument("sourceEntity", EntityArgument.entity())
                        .then(argument("sourceObjective", ObjectiveArgument.objective())
                                .executes(ctx -> ResourceCommand.operation(ctx, operation))));
    }

    private static int has(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        LivingEntity target = getLivingTarget(context);
        if (target == null) return 0;

        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        CommandSourceStack source = context.getSource();
        boolean has = OriginDataHolder.get(target).getComponentFor(power, ResourceComponent.class).isPresent();

        if (has)
            source.sendSuccess(() -> Component.translatable("commands.origins.resource.has.success", target.getName(), RLHelper.string(power)), false);
        else
            source.sendFailure(Component.translatable("commands.origins.resource.has.failure", target.getName(), RLHelper.string(power)));
        return has ? 1 : 0;
    }

    private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        LivingEntity target = getLivingTarget(context);
        if (target == null) return 0;

        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        ResourceComponent component = getResourceComponent(target, power, context);
        if (component == null) return 0;

        context.getSource().sendSuccess(() -> Component.translatable("commands.origins.resource.get.result", target.getName(), RLHelper.string(power), component.getValue()), false);
        return 1;
    }

    private static int change(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        LivingEntity target = getLivingTarget(context);
        if (target == null) return 0;

        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        ResourceComponent component = getResourceComponent(target, power, context);
        if (component == null) return 0;

        int value = IntegerArgumentType.getInteger(context, "value");
        OriginDataHolder holder = OriginDataHolder.get(target);
        component.updateResource(Integer::sum, value);
        holder.sync();
        context.getSource().sendSuccess(() -> Component.translatable("commands.origins.resource.change.success", target.getName(), RLHelper.string(power), value, component.getValue()), true);
        return 1;
    }

    private static int operation(CommandContext<CommandSourceStack> context, Operation operation) throws CommandSyntaxException {
        LivingEntity target = getLivingTarget(context);
        if (target == null) return 0;

        Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
        ResourceComponent component = getResourceComponent(target, power, context);
        if (component == null) return 0;

        Entity sourceEntity = EntityArgument.getEntity(context, "sourceEntity");
        Objective objective = ObjectiveArgument.getObjective(context, "sourceObjective");
        Scoreboard scoreboard = sourceEntity.level().getScoreboard();
        ScoreHolder holder = ScoreHolder.forNameOnly(sourceEntity.getScoreboardName());
        int sourceValue = scoreboard.getOrCreatePlayerScore(holder, objective).get();
        int targetValue = component.getValue();
        int newValue = operation.apply(targetValue, sourceValue);

        component.updateResource(current -> newValue);
        OriginDataHolder.get(target).sync();

        if (operation == Operation.SWAP)
            scoreboard.getOrCreatePlayerScore(holder, objective).set(targetValue);
        else
            scoreboard.getOrCreatePlayerScore(holder, objective).set(sourceValue);

        context.getSource().sendSuccess(() -> Component.translatable("commands.origins.resource.operation.success", target.getName(), RLHelper.string(power), operation.symbol, sourceEntity.getName(), objective.getName(), newValue), true);
        return 1;
    }

    private static LivingEntity getLivingTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, "target");
        if (target instanceof LivingEntity living) return living;
        context.getSource().sendFailure(Component.translatable("commands.origins.resource.invalid_entity"));
        return null;
    }

    private static ResourceComponent getResourceComponent(LivingEntity target, Holder<Power> power, CommandContext<CommandSourceStack> context) {
        ResourceComponent component = OriginDataHolder.get(target).getComponentFor(power, ResourceComponent.class).orElse(null);
        if (component != null) return component;
        context.getSource().sendFailure(Component.translatable("commands.origins.resource.missing_power", target.getName(), RLHelper.string(power)));
        return null;
    }

    private enum Operation {
        MOD("%="),
        MUL("*="),
        ADD("+="),
        SUB("-="),
        DIV("/="),
        MIN("<"),
        SET("="),
        MAX(">"),
        SWAP("><");

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        private int apply(int targetValue, int sourceValue) {
            return switch (this) {
                case MOD -> sourceValue == 0 ? 0 : targetValue % sourceValue;
                case MUL -> targetValue * sourceValue;
                case ADD -> targetValue + sourceValue;
                case SUB -> targetValue - sourceValue;
                case DIV -> sourceValue == 0 ? 0 : targetValue / sourceValue;
                case MIN -> Math.min(targetValue, sourceValue);
                case SET, SWAP -> sourceValue;
                case MAX -> Math.max(targetValue, sourceValue);
            };
        }
    }
}
