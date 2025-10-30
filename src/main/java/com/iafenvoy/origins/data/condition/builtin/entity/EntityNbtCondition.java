package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record EntityNbtCondition(CompoundTag nbt) implements EntityCondition {
    public static final MapCodec<EntityNbtCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CompoundTag.CODEC.fieldOf("nbt").forGetter(EntityNbtCondition::nbt)
    ).apply(i, EntityNbtCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return NbtUtils.compareNbt(this.nbt, entity.saveWithoutId(new CompoundTag()), true);
    }
}
