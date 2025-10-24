package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public record EntityExplodeAction(float power, DestructionType destructionType,
                                  Optional<BlockCondition> indestructible, boolean createFire) implements EntityAction {
    public static final MapCodec<EntityExplodeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("power").forGetter(EntityExplodeAction::power),
            DestructionType.CODEC.optionalFieldOf("destruction_type", DestructionType.BREAK).forGetter(EntityExplodeAction::destructionType),
            BlockCondition.CODEC.optionalFieldOf("indestructible").forGetter(EntityExplodeAction::indestructible),
            Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(EntityExplodeAction::createFire)
    ).apply(i, EntityExplodeAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        Level level = source.level();
        Vec3 pos = source.position();
        //FIXME::Rewrite this
        if (level.isClientSide()) return;
        ExplosionDamageCalculator calculator = this.indestructible().isEmpty() ? new ExplosionDamageCalculator() : new ExplosionDamageCalculator() {
            @Override
            @NotNull
            public Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluid) {
                Optional<Float> def = super.getBlockExplosionResistance(explosion, world, pos, state, fluid);
                Optional<Float> ovr = EntityExplodeAction.this.indestructible.map(x -> x.test(level, pos)).filter(x -> x).map(x -> 100F);
                return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
            }
        };
        level.explode(null, level.damageSources().explosion(null, null), calculator, pos.x, pos.y, pos.z, this.power, this.createFire, Level.ExplosionInteraction.MOB);
    }

    //FIXME::Share enum
    public enum DestructionType implements StringRepresentable {
        NONE(Explosion.BlockInteraction.KEEP),
        BREAK(Explosion.BlockInteraction.DESTROY_WITH_DECAY),
        DESTROY(Explosion.BlockInteraction.DESTROY);
        public static final Codec<DestructionType> CODEC = StringRepresentable.fromEnum(DestructionType::values);
        private final Explosion.BlockInteraction interaction;

        DestructionType(Explosion.BlockInteraction interaction) {
            this.interaction = interaction;
        }

        public Explosion.BlockInteraction getInteraction() {
            return this.interaction;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
