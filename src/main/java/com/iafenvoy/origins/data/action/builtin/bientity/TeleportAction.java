package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record TeleportAction(boolean teleportActor, boolean teleportTarget, boolean rotate) implements BiEntityAction {
    public static final MapCodec<TeleportAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("teleport_actor", false).forGetter(TeleportAction::teleportActor),
            Codec.BOOL.optionalFieldOf("teleport_target", true).forGetter(TeleportAction::teleportTarget),
            Codec.BOOL.optionalFieldOf("rotate", false).forGetter(TeleportAction::rotate)
    ).apply(instance, TeleportAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity actor, @NotNull Entity target) {
        if (actor.level().isClientSide || (!this.teleportActor && !this.teleportTarget)) {
            return;
        }

        Position actorPosition = Position.of(actor);
        Position targetPosition = Position.of(target);
        if (this.teleportActor) {
            targetPosition.teleport(actor, this.rotate);
        }
        if (this.teleportTarget) {
            actorPosition.teleport(target, this.rotate);
        }
    }

    private record Position(ServerLevel level, double x, double y, double z, float yRot, float xRot) {
        private static Position of(Entity entity) {
            return new Position((ServerLevel) entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        }

        private void teleport(Entity entity, boolean rotate) {
            entity.teleportTo(this.level, this.x, this.y, this.z, Set.of(), rotate ? this.yRot : entity.getYRot(), rotate ? this.xRot : entity.getXRot());
        }
    }
}
