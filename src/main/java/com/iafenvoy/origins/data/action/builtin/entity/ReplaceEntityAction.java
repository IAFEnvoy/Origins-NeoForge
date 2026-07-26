package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record ReplaceEntityAction(List<EntityType<?>> entityTypes, boolean random, Optional<CompoundTag> tag) implements EntityAction {
    public static final MapCodec<ReplaceEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().fieldOf("entity_types").forGetter(ReplaceEntityAction::entityTypes),
            Codec.BOOL.optionalFieldOf("random", false).forGetter(ReplaceEntityAction::random),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(ReplaceEntityAction::tag)
    ).apply(instance, ReplaceEntityAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity entity) {
        if (entity instanceof Player || !(entity.level() instanceof ServerLevel level) || this.entityTypes.isEmpty()) {
            return;
        }
        EntityType<?> type = this.random ? this.entityTypes.get(level.random.nextInt(this.entityTypes.size())) : this.entityTypes.getFirst();
        Entity replacement = type.create(level);
        if (replacement == null) {
            return;
        }
        CompoundTag data = entity.saveWithoutId(new CompoundTag());
        this.tag.ifPresent(data::merge);
        replacement.load(data);
        replacement.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        entity.discard();
        level.addFreshEntity(replacement);
    }
}
