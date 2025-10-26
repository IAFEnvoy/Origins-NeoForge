package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

//FIXME::No optional
public record BlockExplodeAction(float power, DestructionType destructionType,
                                 Optional<BlockCondition> indestructible, boolean createFire) implements BlockAction {
    public static final MapCodec<BlockExplodeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("power").forGetter(BlockExplodeAction::power),
            DestructionType.CODEC.optionalFieldOf("destruction_type", DestructionType.BREAK).forGetter(BlockExplodeAction::destructionType),
            BlockCondition.CODEC.optionalFieldOf("indestructible").forGetter(BlockExplodeAction::indestructible),
            Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(BlockExplodeAction::createFire)
    ).apply(i, BlockExplodeAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        //FIXME::Rewrite this
        if (level.isClientSide()) return;
        ExplosionDamageCalculator calculator = this.indestructible().isEmpty() ? new ExplosionDamageCalculator() : new ExplosionDamageCalculator() {
            @Override
            @NotNull
            public Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluid) {
                Optional<Float> def = super.getBlockExplosionResistance(explosion, world, pos, state, fluid);
                Optional<Float> ovr = BlockExplodeAction.this.indestructible.map(x -> x.test(level, pos)).filter(x -> x).map(x -> 100F);
                return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
            }
        };
        level.explode(null, level.damageSources().explosion(null, null), calculator, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.power, this.createFire, Level.ExplosionInteraction.MOB);
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
