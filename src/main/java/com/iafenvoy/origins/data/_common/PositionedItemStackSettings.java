package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.OptionalInt;
import java.util.function.Function;

public record PositionedItemStackSettings(ItemStack stack, OptionalInt slot) {
    public static final Codec<PositionedItemStackSettings> CODEC = RecordCodecBuilder.create(i -> i.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(PositionedItemStackSettings::stack),
            MiscCodecs.integer("slot").forGetter(PositionedItemStackSettings::slot)
    ).apply(i, PositionedItemStackSettings::new));
    public static final Codec<PositionedItemStackSettings> COMBINED_CODEC = Codec.either(ItemStack.CODEC, CODEC)
            .xmap(e -> e.map(x -> new PositionedItemStackSettings(x, OptionalInt.empty()), Function.identity()), Either::right);
}
