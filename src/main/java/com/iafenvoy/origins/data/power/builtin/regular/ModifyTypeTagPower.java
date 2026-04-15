package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ModifyTypeTagPower extends Power {
    public static final MapCodec<ModifyTypeTagPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.STRING.fieldOf("tag").forGetter(ModifyTypeTagPower::getTag)
    ).apply(i, ModifyTypeTagPower::new));
    private final String tag;

    public ModifyTypeTagPower(BaseSettings settings, String tag) {
        super(settings);
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
