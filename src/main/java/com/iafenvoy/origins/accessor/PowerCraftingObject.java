package com.iafenvoy.origins.accessor;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PowerCraftingObject {
    @Nullable
    Player origins$getPlayer();

    void origins$setPlayer(Player player);

}
