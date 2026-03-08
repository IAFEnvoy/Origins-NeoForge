package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.builtin.DestructionType;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ExplodeAction(float power, DestructionType destructionType,
                            Optional<BlockCondition> indestructible, boolean createFire) implements BlockAction {
    public static final MapCodec<ExplodeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("power").forGetter(ExplodeAction::power),
            DestructionType.CODEC.optionalFieldOf("destruction_type", DestructionType.BREAK).forGetter(ExplodeAction::destructionType),
            BlockCondition.CODEC.optionalFieldOf("indestructible").forGetter(ExplodeAction::indestructible),
            Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(ExplodeAction::createFire)
    ).apply(i, ExplodeAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (level.isClientSide()) return;
        ExplosionDamageCalculator calculator = this.indestructible
                .map(condition -> (ExplosionDamageCalculator) new ExplosionDamageCalculator() {
                    @Override
                    @NotNull
                    public Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos blockPos, @NotNull BlockState state, @NotNull FluidState fluid) {
                        Optional<Float> def = super.getBlockExplosionResistance(explosion, world, blockPos, state, fluid);
                        if (condition.test(level, blockPos)) {
                            return Optional.of(def.map(d -> Math.max(d, 100F)).orElse(100F));
                        }
                        return def;
                    }
                })
                .orElseGet(ExplosionDamageCalculator::new);
        level.explode(null, level.damageSources().explosion(null, null), calculator, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.power, this.createFire, this.destructionType.getInteraction());
    }
}
