package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SpawnEntityAction(EntityType<?> entityType, Optional<CompoundTag> tag,
                                Optional<EntityAction> entityAction,
                                Optional<BiEntityAction> biEntityAction) implements EntityAction {
    public static final MapCodec<SpawnEntityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(SpawnEntityAction::entityType),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(SpawnEntityAction::tag),
            EntityAction.CODEC.optionalFieldOf("entity_action").forGetter(SpawnEntityAction::entityAction),
            BiEntityAction.CODEC.optionalFieldOf("bientity_action").forGetter(SpawnEntityAction::biEntityAction)
    ).apply(i, SpawnEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source.level() instanceof ServerLevel serverLevel)
            this.entityType.spawn(serverLevel, c -> {
                this.tag.ifPresent(c::load);
                this.entityAction.ifPresent(x -> x.accept(c));
                this.biEntityAction.ifPresent(x -> x.accept(source, c));
            }, source.blockPosition(), MobSpawnType.MOB_SUMMONED, false, false);
    }
}
