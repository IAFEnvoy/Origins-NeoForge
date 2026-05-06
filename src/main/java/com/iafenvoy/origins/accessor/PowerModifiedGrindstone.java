package com.iafenvoy.origins.accessor;

import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PowerModifiedGrindstone {
    List<ModifyGrindstonePower> origins$getAppliedPowers();

    Player origins$getPlayer();

    @Nullable
    BlockPos origins$getPos();

}
