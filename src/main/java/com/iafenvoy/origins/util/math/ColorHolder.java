package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record ColorHolder(Optional<Float> r, Optional<Float> g, Optional<Float> b, Optional<Float> a) {
    public static final MapCodec<ColorHolder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("r").forGetter(ColorHolder::r),
            Codec.FLOAT.optionalFieldOf("g").forGetter(ColorHolder::g),
            Codec.FLOAT.optionalFieldOf("b").forGetter(ColorHolder::b),
            Codec.FLOAT.optionalFieldOf("a").forGetter(ColorHolder::a),
            Codec.INT.optionalFieldOf("color").forGetter(x -> Optional.of(x.getIntValue()))
    ).apply(i, ColorHolder::of));

    private static ColorHolder of(Optional<Float> r, Optional<Float> g, Optional<Float> b, Optional<Float> a, Optional<Integer> color) {
        return color.map(ColorHolder::of).orElseGet(() -> new ColorHolder(r, g, b, a));
    }

    public static ColorHolder of(float r, float g, float b, float a) {
        return new ColorHolder(Optional.of(r), Optional.of(g), Optional.of(b), Optional.of(a));
    }

    public static ColorHolder of(float r, float g, float b) {
        return new ColorHolder(Optional.of(r), Optional.of(g), Optional.of(b), Optional.empty());
    }

    public static ColorHolder empty() {
        return new ColorHolder(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static ColorHolder of(int color) {
        return new ColorHolder(
                Optional.of(((color >> 16) & 0xFF) / 255f),
                Optional.of(((color >> 8) & 0xFF) / 255f),
                Optional.of((color & 0xFF) / 255f),
                Optional.of(((color >> 24) & 0xFF) / 255f)
        );
    }

    public int getIntValue() {
        return ((int) (this.a.orElse(1f) * 255) << 24) |
                ((int) (this.r.orElse(0f) * 255) << 16) |
                ((int) (this.g.orElse(0f) * 255) << 8) |
                (int) (this.b.orElse(0f) * 255);
    }

    public ColorHolder merge(int color) {
        return this.merge(ColorHolder.of(color));
    }

    public ColorHolder merge(ColorHolder another) {
        return new ColorHolder(
                mergeComponent(this.r, another.r),
                mergeComponent(this.g, another.g),
                mergeComponent(this.b, another.b),
                mergeComponent(this.a, another.a)
        );
    }

    private static Optional<Float> mergeComponent(Optional<Float> c1, Optional<Float> c2) {
        if (c1.isPresent() && c2.isPresent()) return Optional.of(c1.get() * c2.get());
        else if (c1.isPresent()) return c1;
        else return c2;
    }
}
