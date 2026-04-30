package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.OptionalInt;

public record PositionedItemStackSettings(ItemStack stack, OptionalInt slot) {
    public static final Codec<PositionedItemStackSettings> CODEC = RecordCodecBuilder.create(i -> i.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(PositionedItemStackSettings::stack),
            OptionalCodecs.integer("slot").forGetter(PositionedItemStackSettings::slot)
    ).apply(i, PositionedItemStackSettings::new));
}
