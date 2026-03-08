package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.builtin.DestructionType;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ExplodeAction(float power, DestructionType destructionType,
                            Optional<BlockCondition> indestructible, boolean createFire) implements EntityAction {
    public static final MapCodec<ExplodeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.fieldOf("power").forGetter(ExplodeAction::power),
            DestructionType.CODEC.optionalFieldOf("destruction_type", DestructionType.BREAK).forGetter(ExplodeAction::destructionType),
            BlockCondition.CODEC.optionalFieldOf("indestructible").forGetter(ExplodeAction::indestructible),
            Codec.BOOL.optionalFieldOf("create_fire", false).forGetter(ExplodeAction::createFire)
    ).apply(i, ExplodeAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        Level level = source.level();
        if (level.isClientSide()) return;
        Vec3 pos = source.position();
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
        level.explode(source, level.damageSources().explosion(source, source), calculator, pos.x, pos.y, pos.z, this.power, this.createFire, this.destructionType.getInteraction());
    }
}
