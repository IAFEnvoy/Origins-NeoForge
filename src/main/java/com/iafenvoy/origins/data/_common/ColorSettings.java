package com.iafenvoy.origins.data._common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record ColorSettings(Optional<Float> r, Optional<Float> g, Optional<Float> b, Optional<Float> a) {
    public static final MapCodec<ColorSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("red").forGetter(ColorSettings::r),
            Codec.FLOAT.optionalFieldOf("green").forGetter(ColorSettings::g),
            Codec.FLOAT.optionalFieldOf("blue").forGetter(ColorSettings::b),
            Codec.FLOAT.optionalFieldOf("alpha").forGetter(ColorSettings::a),
            Codec.INT.optionalFieldOf("color").forGetter(x -> Optional.of(x.getIntValue()))
    ).apply(i, ColorSettings::of));
    public static final MapCodec<ColorSettings> NO_ALPHA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("red").forGetter(ColorSettings::r),
            Codec.FLOAT.optionalFieldOf("green").forGetter(ColorSettings::g),
            Codec.FLOAT.optionalFieldOf("blue").forGetter(ColorSettings::b),
            Codec.INT.optionalFieldOf("color").forGetter(x -> Optional.of(x.getIntValue()))
    ).apply(i, ColorSettings::of));

    private static ColorSettings of(Optional<Float> r, Optional<Float> g, Optional<Float> b, Optional<Float> a, Optional<Integer> color) {
        return color.map(ColorSettings::of).orElseGet(() -> new ColorSettings(r, g, b, a));
    }

    private static ColorSettings of(Optional<Float> r, Optional<Float> g, Optional<Float> b, Optional<Integer> color) {
        return color.map(ColorSettings::of).orElseGet(() -> new ColorSettings(r, g, b, Optional.empty()));
    }

    public static ColorSettings of(float r, float g, float b, float a) {
        return new ColorSettings(Optional.of(r), Optional.of(g), Optional.of(b), Optional.of(a));
    }

    public static ColorSettings of(float r, float g, float b) {
        return new ColorSettings(Optional.of(r), Optional.of(g), Optional.of(b), Optional.empty());
    }

    public static ColorSettings empty() {
        return new ColorSettings(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static ColorSettings of(int color) {
        return new ColorSettings(
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

    public ColorSettings merge(int color) {
        return this.merge(ColorSettings.of(color));
    }

    public ColorSettings merge(ColorSettings another) {
        return new ColorSettings(
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

    public ColorSettings withAlpha(float alpha) {
        return new ColorSettings(this.r, this.g, this.b, Optional.of(alpha));
    }

    public ColorSettings multiply(float strength) {
        return new ColorSettings(
                this.r.map(r -> r * strength),
                this.g.map(g -> g * strength),
                this.b.map(b -> b * strength),
                this.a.map(a -> a * strength)
        );
    }
}
