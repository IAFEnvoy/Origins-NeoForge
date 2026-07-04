package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber
public class ModifyPlayerSpawnPower extends Power {
    public static final MapCodec<ModifyPlayerSpawnPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(ModifyPlayerSpawnPower::getDimension),
            Codec.FLOAT.optionalFieldOf("dimension_distance_multiplier", 1F).forGetter(ModifyPlayerSpawnPower::getDistanceMultiplier),
            SpawnStrategy.CODEC.optionalFieldOf("spawn_strategy", SpawnStrategy.DEFAULT).forGetter(ModifyPlayerSpawnPower::getSpawnStrategy),
            //FIXME::Can work?
            Codec.either(ResourceKey.codec(Registries.BIOME), TagKey.hashedCodec(Registries.BIOME)).optionalFieldOf("biome").forGetter(ModifyPlayerSpawnPower::getBiome),
            Codec.either(ResourceKey.codec(Registries.STRUCTURE), TagKey.hashedCodec(Registries.STRUCTURE)).optionalFieldOf("structure").forGetter(ModifyPlayerSpawnPower::getStructure)
    ).apply(i, ModifyPlayerSpawnPower::new));
    private final ResourceKey<Level> dimension;
    private final float distanceMultiplier;
    private final SpawnStrategy spawnStrategy;
    private final Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome;
    private final Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> structure;

    public ModifyPlayerSpawnPower(BaseSettings settings, ResourceKey<Level> dimension, float distanceMultiplier, SpawnStrategy spawnStrategy, Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome, Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> structure) {
        super(settings);
        this.dimension = dimension;
        this.distanceMultiplier = distanceMultiplier;
        this.spawnStrategy = spawnStrategy;
        this.biome = biome;
        this.structure = structure;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    public float getDistanceMultiplier() {
        return this.distanceMultiplier;
    }

    public SpawnStrategy getSpawnStrategy() {
        return this.spawnStrategy;
    }

    public Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> getBiome() {
        return this.biome;
    }

    public Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getStructure() {
        return this.structure;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        if (!(holder.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if (!serverPlayer.hasDisconnected() && serverPlayer.getRespawnPosition() != null && !serverPlayer.isRespawnForced())
            serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
    }

    @SubscribeEvent
    public static void preventEndExitSpawnPointResetting(PlayerRespawnPositionEvent event) {
        event.setCopyOriginalSpawnPosition(((EndRespawningEntity) event.getEntity()).origins$hasRealRespawnPoint());
    }

    public Optional<Tuple<ServerLevel, BlockPos>> getSpawn(Entity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) return Optional.empty();
        MinecraftServer server = serverPlayer.server;
        ServerLevel targetDimension = server.getLevel(this.dimension);
        if (targetDimension == null) return Optional.empty();
        int center = targetDimension.getLogicalHeight() / 2, range = 64;

        AtomicReference<Vec3> newSpawnPointVec = new AtomicReference<>();
        BlockPos dimensionSpawnPos = serverPlayer.serverLevel().getSharedSpawnPos();

        BlockPos.MutableBlockPos newSpawnPointPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableDimensionSpawnPos = this.spawnStrategy.apply(dimensionSpawnPos, center, this.distanceMultiplier).mutable();

        this.getBiomePos(entity, targetDimension, mutableDimensionSpawnPos).ifPresent(mutableDimensionSpawnPos::set);
        this.getSpawnPos(entity, targetDimension, mutableDimensionSpawnPos, range).ifPresent(newSpawnPointVec::set);

        if (newSpawnPointVec.get() == null) return Optional.empty();

        Vec3 msp = newSpawnPointVec.get();
        newSpawnPointPos.set(msp.x, msp.y, msp.z);

        targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(newSpawnPointPos), 11, Unit.INSTANCE);
        return Optional.of(new Tuple<>(targetDimension, newSpawnPointPos));
    }

    private Optional<BlockPos> getBiomePos(Entity entity, ServerLevel targetDimension, BlockPos originPos) {
        if (this.biome.isEmpty()) return Optional.empty();
        int radius = OriginsConfig.INSTANCE.modifyPlayerSpawnPower.radius.getValue();
        int horizontalBlockCheckInterval = OriginsConfig.INSTANCE.modifyPlayerSpawnPower.horizontalBlockCheckInterval.getValue();
        int verticalBlockCheckInterval = OriginsConfig.INSTANCE.modifyPlayerSpawnPower.verticalBlockCheckInterval.getValue();
        if (radius < 0) radius = 6400;
        if (horizontalBlockCheckInterval <= 0) horizontalBlockCheckInterval = 64;
        if (verticalBlockCheckInterval <= 0) verticalBlockCheckInterval = 64;

        Pair<BlockPos, Holder<Biome>> targetBiomePos = targetDimension.findClosestBiome3d(
                biome -> this.biome.map(x -> x.map(biome::is, biome::is)).orElse(false),
                originPos,
                radius,
                horizontalBlockCheckInterval,
                verticalBlockCheckInterval
        );

        if (targetBiomePos != null) return Optional.of(targetBiomePos.getFirst());
        else {
            StringBuilder name = new StringBuilder();
            this.biome.ifPresent(x -> x.map(key -> name.append("biome \"").append(key.location()).append("\""), tag -> name.append(!name.isEmpty() ? " or " : "").append("any biomes from tag \"").append(tag.location()).append("\"")));

            RegistryAccess access = targetDimension.registryAccess();
            Origins.LOGGER.warn("Power \"{}\" could not set player {}'s spawn point at {} as no matched biome can be found nearby in dimension \"{}\".", this.getId(access), entity.getName().getString(), name, this.dimension.location());
            entity.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(this.getId(access), name, this.dimension.location())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

            return Optional.empty();
        }
    }

    private Optional<Tuple<BlockPos, Structure>> getStructurePos(Entity entity, ServerLevel dimension) {
        if (this.structure.isEmpty()) return Optional.empty();

        Registry<Structure> structureRegistry = dimension.registryAccess().registryOrThrow(Registries.STRUCTURE);
        List<Holder<Structure>> structureEntries = new ArrayList<>();

        this.structure.ifPresent(x -> x.
                ifLeft(key -> structureEntries.add(structureRegistry.getHolderOrThrow(key)))
                .ifRight(tag -> structureRegistry.getTag(tag).ifPresent(h -> h.stream().forEach(structureEntries::add)))
        );

        BlockPos center = new BlockPos(0, 70, 0);
        int radius = OriginsConfig.INSTANCE.modifyPlayerSpawnPower.radius.getValue();
        if (radius < 0) radius = 6400;


        Optional<Tuple<BlockPos, Structure>> result = Optional.ofNullable(dimension.getChunkSource().getGenerator().findNearestMapStructure(dimension, HolderSet.direct(structureEntries), center, radius, false))
                .map(pair -> pair.mapSecond(Holder::value))
                .map(pair -> new Tuple<>(pair.getFirst(), pair.getSecond()));

        if (result.isEmpty()) {

            StringBuilder name = new StringBuilder();
            this.structure.ifPresent(x -> x
                    .ifLeft(key -> name.append("structure \"").append(key.location()).append("\""))
                    .ifRight(tag -> name.append(!name.isEmpty() ? " or " : "").append("any structures from tag \"").append(tag.location()).append("\""))
            );
            RegistryAccess access = dimension.registryAccess();
            Origins.LOGGER.warn("Power \"{}\" could not set player {}'s spawn point at {} as no matched structure can be found nearby in dimension \"{}\".", this.getId(access), entity.getName().getString(), name, this.dimension.location());
            entity.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(this.getId(access), name, this.dimension.location())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

            return Optional.empty();
        }
        return result;
    }

    private Optional<Vec3> getSpawnPos(Entity entity, ServerLevel targetDimension, BlockPos originPos, int range) {
        if (this.structure.isEmpty()) return this.getValidSpawn(entity, targetDimension, originPos, range);

        Optional<Tuple<BlockPos, Structure>> targetStructure = this.getStructurePos(entity, targetDimension);
        if (targetStructure.isEmpty()) return Optional.empty();

        BlockPos structurePos = targetStructure.get().getA();
        Structure structure = targetStructure.get().getB();

        ChunkPos chunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
        SectionPos chunkSectionPos = SectionPos.of(chunkPos, 0);

        return Optional.ofNullable(targetDimension.structureManager().getStartForStructure(chunkSectionPos, structure, targetDimension.getChunk(structurePos)))
                .map(structureStart -> structureStart.getBoundingBox().getCenter())
                .flatMap(pos -> this.getValidSpawn(entity, targetDimension, pos, range));
    }

    private Optional<Vec3> getValidSpawn(Entity entity, ServerLevel targetDimension, BlockPos startPos, int range) {
        //  The 'direction' vector that determines the direction of the iteration
        int dx = 1;
        int dz = 0;

        //  The length of the current segment
        int segmentLength = 1;

        //  The center of the structure/dimension
        int center = startPos.getY();

        //  The valid spawn position and (mutable) starting position
        Vec3 spawnPos;
        BlockPos.MutableBlockPos mutableStartPos = startPos.mutable();

        //  The current position
        int x = startPos.getX();
        int z = startPos.getZ();

        //  Determines how much of the current segment has been passed
        int segmentPassed = 0;

        //  Vertical offsets
        int upOffset = 0;
        int downOffset = 0;

        //  The min and max Y values of the target dimension
        int maxY = targetDimension.getLogicalHeight();
        int minY = targetDimension.dimensionTypeRegistration().value().minY();

        while (upOffset < maxY || downOffset > minY) {

            for (int steps = 0; steps < range; ++steps) {

                //  Make a step by adding the 'direction' vector to the current position
                x += dx;
                z += dz;
                mutableStartPos.setX(x);
                mutableStartPos.setZ(z);

                //  Increment how much of the current segment has been passed
                ++segmentPassed;

                //  Offset the Y axis (up and down) of the current position to check for valid spawn positions
                mutableStartPos.setY(center + upOffset);
                spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);

                if (spawnPos != null) {
                    return Optional.of(spawnPos);
                }

                mutableStartPos.setY(center + downOffset);
                spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);

                if (spawnPos != null) {
                    return Optional.of(spawnPos);
                }

                //  If the current segment has not been passed, continue the loop
                if (segmentPassed != segmentLength) continue;

                //  Otherwise, reset the value of how much of the current segment has been passed
                segmentPassed = 0;

                //  'Rotate' the 'direction' vector
                int bdx = dx;
                dx = -dz;
                dz = bdx;

                //  Increment the length of the current segment if necessary
                if (dz == 0) ++segmentLength;

            }

            //  Increment/decrement the up/down offsets until it's no longer less/greater than the max/min Y
            if (upOffset < maxY) {
                upOffset++;
            }

            if (downOffset > minY) {
                downOffset--;
            }

        }

        return Optional.empty();

    }

    public enum SpawnStrategy implements StringRepresentable {
        CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
        DEFAULT((blockPos, center, multiplier) -> {
            BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
            if (multiplier != 0)
                mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
            else mut.set(blockPos);
            return mut;
        });
        public static final Codec<SpawnStrategy> CODEC = StringRepresentable.fromValues(SpawnStrategy::values);
        private final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;

        SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
            this.strategyApplier = strategyApplier;
        }

        public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
            return this.strategyApplier.apply(blockPos, center, multiplier);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
