package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.data._common.BlockPlaceSettings;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
