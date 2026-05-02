package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
public class ModifyPlayerSpawnPower extends Power {
    public static final MapCodec<ModifyPlayerSpawnPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(ModifyPlayerSpawnPower::getDimension),
            Codec.FLOAT.optionalFieldOf("dimension_distance_multiplier", 0F).forGetter(ModifyPlayerSpawnPower::getDistanceMultiplier),
            ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(ModifyPlayerSpawnPower::getBiome),
            ExtraEnumCodecs.enumCodec(SpawnStrategy::valueOf).optionalFieldOf("spawn_strategy", SpawnStrategy.DEFAULT).forGetter(ModifyPlayerSpawnPower::getSpawnStrategy),
            ResourceKey.codec(Registries.STRUCTURE).optionalFieldOf("structure").forGetter(ModifyPlayerSpawnPower::getStructure)
    ).apply(i, ModifyPlayerSpawnPower::new));
    private final ResourceKey<Level> dimension;
    private final float distanceMultiplier;
    private final Optional<ResourceKey<Biome>> biome;
    private final SpawnStrategy spawnStrategy;
    private final Optional<ResourceKey<Structure>> structure;

    public ModifyPlayerSpawnPower(BaseSettings settings, ResourceKey<Level> dimension, float distanceMultiplier, Optional<ResourceKey<Biome>> biome, SpawnStrategy spawnStrategy, Optional<ResourceKey<Structure>> structure) {
        super(settings);
        this.dimension = dimension;
        this.distanceMultiplier = distanceMultiplier;
        this.biome = biome;
        this.spawnStrategy = spawnStrategy;
        this.structure = structure;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public float getDistanceMultiplier() {
        return this.distanceMultiplier;
    }

    public Optional<ResourceKey<Biome>> getBiome() {
        return this.biome;
    }

    public SpawnStrategy getSpawnStrategy() {
        return this.spawnStrategy;
    }

    public Optional<ResourceKey<Structure>> getStructure() {
        return this.structure;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public enum SpawnStrategy {
        CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
        DEFAULT(
                (blockPos, center, multiplier) -> {
                    BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
                    if (multiplier != 0)
                        mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
                    else mut.set(blockPos);
                    return mut;

                }
        );

        final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;

        SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
            this.strategyApplier = strategyApplier;
        }

        public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
            return this.strategyApplier.apply(blockPos, center, multiplier);
        }
    }
}
