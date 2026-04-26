package com.iafenvoy.origins.data.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record CooldownSettings(int cooldown, Optional<HudRender> hudRender) {
    public static final MapCodec<CooldownSettings> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("cooldown", 1).forGetter(CooldownSettings::cooldown),
            HudRender.CODEC.optionalFieldOf("hud_render").forGetter(CooldownSettings::hudRender)
    ).apply(i, CooldownSettings::new));
}
