package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data._common.BlockPlaceSettings;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

@NotImplementedYet
public class ActionOnBlockPlacePower extends Power {
    public static final MapCodec<ActionOnBlockPlacePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(ActionOnBlockPlacePower::getSettings),
            BlockPlaceSettings.CODEC.forGetter(ActionOnBlockPlacePower::getBlockPlaceSettings)
    ).apply(i, ActionOnBlockPlacePower::new));
    private final BlockPlaceSettings blockPlaceSettings;

    public ActionOnBlockPlacePower(BaseSettings settings, BlockPlaceSettings blockPlaceSettings) {
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
