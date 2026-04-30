package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.data._common.BlockPlaceSettings;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnBlockPlacePower;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class PreventBlockPlacePower extends Power {
    public static final MapCodec<PreventBlockPlacePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(PreventBlockPlacePower::getSettings),
            BlockPlaceSettings.CODEC.forGetter(PreventBlockPlacePower::getBlockPlaceSettings)
    ).apply(i, PreventBlockPlacePower::new));
    private final BlockPlaceSettings blockPlaceSettings;

    public PreventBlockPlacePower(BaseSettings settings, BlockPlaceSettings blockPlaceSettings) {
        super(settings);
        this.blockPlaceSettings = blockPlaceSettings;
    }

    public BlockPlaceSettings getBlockPlaceSettings() {
        return this.blockPlaceSettings;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
