package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SpawnEntityAction(EntityType<?> entityType, Optional<CompoundTag> tag,
                                EntityAction entityAction) implements BlockAction {
    public static final MapCodec<SpawnEntityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(SpawnEntityAction::entityType),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(SpawnEntityAction::tag),
            EntityAction.optionalCodec("entity_action").forGetter(SpawnEntityAction::entityAction)
    ).apply(i, SpawnEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (level instanceof ServerLevel serverLevel) {
            Entity entity = this.entityType.spawn(serverLevel, x -> this.tag.ifPresent(x::load), pos, MobSpawnType.MOB_SUMMONED, false, false);
            if (entity != null) this.entityAction.execute(entity);
        }
    }
}
