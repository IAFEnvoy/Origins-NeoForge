package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.util.Space;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.function.BiFunction;

public record AddVelocityAction(float x, float y, float z, Reference reference, boolean client, boolean server,
                                boolean set) implements BiEntityAction {
    public static final MapCodec<AddVelocityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("x", 0F).forGetter(AddVelocityAction::x),
            Codec.FLOAT.optionalFieldOf("y", 0F).forGetter(AddVelocityAction::y),
            Codec.FLOAT.optionalFieldOf("z", 0F).forGetter(AddVelocityAction::z),
            Reference.CODEC.optionalFieldOf("reference", Reference.POSITION).forGetter(AddVelocityAction::reference),
            Codec.BOOL.optionalFieldOf("client", true).forGetter(AddVelocityAction::client),
            Codec.BOOL.optionalFieldOf("server", true).forGetter(AddVelocityAction::server),
            Codec.BOOL.optionalFieldOf("set", false).forGetter(AddVelocityAction::set)
    ).apply(i, AddVelocityAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source, @NotNull Entity target) {
        //FIXME::May not work properly
        Vector3f velocity = new Vector3f(this.x, this.y, this.z);
        Vec3 refVec = this.reference.apply(source, target);
        //FIXME::Re-implement this method in a simple way.
        Space.transformVectorToBase(refVec, velocity, source.getYRot(), true);
        if (this.set) target.setDeltaMovement(new Vec3(velocity));
        else target.addDeltaMovement(new Vec3(velocity));
        target.hurtMarked = true;
    }

    public enum Reference implements StringRepresentable {
        POSITION((actor, target) -> target.position().subtract(actor.position())),
        ROTATION((actor, target) -> {
            float pitch = actor.getXRot();
            float yaw = actor.getYRot();

            float i = 0.017453292F;

            float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
            float k = -Mth.sin(pitch * i);
            float l = Mth.cos(yaw * i) * Mth.cos(pitch * i);

            return new Vec3(j, k, l);
        });
        public static final Codec<Reference> CODEC = StringRepresentable.fromEnum(Reference::values);

        final BiFunction<Entity, Entity, Vec3> refFunction;

        Reference(BiFunction<Entity, Entity, Vec3> refFunction) {
            this.refFunction = refFunction;
        }

        public Vec3 apply(Entity actor, Entity target) {
            return this.refFunction.apply(actor, target);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
