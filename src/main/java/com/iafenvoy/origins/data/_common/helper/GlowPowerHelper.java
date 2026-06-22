package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface GlowPowerHelper {
    EntityCondition getEntityCondition();

    BiEntityCondition getBiEntityCondition();

    boolean shouldUseTeam();

    int getColor();

    boolean canGlow(Player player, Entity entity);
}
