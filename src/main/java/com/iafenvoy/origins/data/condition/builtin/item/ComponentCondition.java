package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ComponentCondition(DataComponentType<?> component, CompoundTag nbt) implements ItemCondition {
    public static final MapCodec<ComponentCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter(ComponentCondition::component),
            CompoundTag.CODEC.fieldOf("nbt").forGetter(ComponentCondition::nbt)
    ).apply(i, ComponentCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        return NbtUtils.compareNbt(this.nbt, getNbt(this.component, stack), true);
    }

    public static <T> Tag getNbt(DataComponentType<T> type, ItemStack stack) {
        return type.codecOrThrow().encodeStart(NbtOps.INSTANCE, stack.get(type)).mapOrElse(x -> x, x -> new CompoundTag());
    }
}
