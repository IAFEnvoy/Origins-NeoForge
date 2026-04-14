package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.common.RaycastSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.util.CommandHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RaycastAction(RaycastSettings settings, EntityAction beforeAction, BiEntityCondition biEntityCondition,
                            CommandInfo commandInfo, HitAction action) implements EntityAction {
    public static final MapCodec<RaycastAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RaycastSettings.CODEC.forGetter(RaycastAction::settings),
            EntityAction.optionalCodec("before_action").forGetter(RaycastAction::beforeAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RaycastAction::biEntityCondition),
            CommandInfo.MAP_CODEC.forGetter(RaycastAction::commandInfo),
            HitAction.MAP_CODEC.forGetter(RaycastAction::action)
    ).apply(instance, RaycastAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        this.beforeAction().execute(source);
        Vec3 direction = source.getViewVector(1);
        Vec3 origin = new Vec3(source.getX(), source.getEyeY(), source.getZ());
        HitResult hitResult = this.settings().perform(source, origin, direction, this.biEntityCondition());
        CommandInfo commandInfo = this.commandInfo();
        HitAction actions = this.action();
        if (hitResult.getType() != HitResult.Type.MISS) {
            if (commandInfo.commandAtHit().isPresent()) {
                Vec3 offsetDirection = direction;
                double offset = 0;
                Vec3 hitPos = hitResult.getLocation();
                if (commandInfo.commandHitOffset().isPresent()) {
                    offset = commandInfo.commandHitOffset().get();
                } else {
                    if (hitResult instanceof BlockHitResult bhr) {
                        if (bhr.getDirection() == Direction.DOWN) {
                            offset = source.getBbHeight();
                        } else if (bhr.getDirection() == Direction.UP) {
                            offset = 0;
                        } else {
                            offset = source.getBbWidth() / 2;
                            offsetDirection = new Vec3(
                                    -bhr.getDirection().getStepX(),
                                    -bhr.getDirection().getStepY(),
                                    -bhr.getDirection().getStepZ()
                            ).reverse();
                        }
                    }
                    offset += 0.05;
                }
                Vec3 at = hitPos.subtract(offsetDirection.scale(offset));
                executeCommandAtHit(source, at, commandInfo.commandAtHit().get());
            }
            if (commandInfo.commandAlongRay().isPresent()) {
                executeStepCommands(source, origin, hitResult.getLocation(), commandInfo.commandAlongRay().get(), commandInfo.commandStep());
            }
            if (hitResult instanceof BlockHitResult bhr)
                actions.blockAction().execute(source.level(), bhr.getBlockPos(), bhr.getDirection());
            if (hitResult instanceof EntityHitResult ehr)
                actions.biEntityAction().execute(source, ehr.getEntity());
            actions.hitAction().execute(source);
        } else {
            if (commandInfo.commandAlongRay().isPresent() && !commandInfo.commandAlongRayOnlyOnHit())
                executeStepCommands(source, origin, hitResult.getLocation(), commandInfo.commandAlongRay().get(), commandInfo.commandStep());
            actions.missAction().execute(source);
        }
    }

    private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
        Vec3 direction = target.subtract(origin).normalize();
        double length = origin.distanceTo(target);
        for (double current = 0; current < length; current += step)
            CommandHelper.executeAt(entity, origin.add(direction.scale(current)), command);
    }

    private static void executeCommandAtHit(Entity entity, Vec3 hitPosition, String command) {
        CommandHelper.executeAt(entity, hitPosition, command);
    }

    public record CommandInfo(Optional<String> commandAtHit, Optional<Double> commandHitOffset,
                              Optional<String> commandAlongRay, double commandStep, boolean commandAlongRayOnlyOnHit) {
        private static final MapCodec<CommandInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("command_at_hit").forGetter(CommandInfo::commandAtHit),
                Codec.DOUBLE.optionalFieldOf("command_hit_offset").forGetter(CommandInfo::commandHitOffset),
                Codec.STRING.optionalFieldOf("command_along_ray").forGetter(CommandInfo::commandAlongRay),
                Codec.DOUBLE.optionalFieldOf("command_step", 1.0).forGetter(CommandInfo::commandStep),
                Codec.BOOL.optionalFieldOf("command_along_ray_only_on_hit", false).forGetter(CommandInfo::commandAlongRayOnlyOnHit)
        ).apply(instance, CommandInfo::new));
    }

    public record HitAction(BlockAction blockAction, EntityAction hitAction, EntityAction missAction,
                            BiEntityAction biEntityAction) {
        private static final MapCodec<HitAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockAction.optionalCodec("block_action").forGetter(HitAction::blockAction),
                EntityAction.optionalCodec("hit_action").forGetter(HitAction::hitAction),
                EntityAction.optionalCodec("miss_action").forGetter(HitAction::missAction),
                BiEntityAction.optionalCodec("bientity_action").forGetter(HitAction::biEntityAction)
        ).apply(instance, HitAction::new));
    }
}
