package com.iafenvoy.origins.accessor;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface PowerCraftingObject {
    Optional<Player> origins$getPlayer();

    void origins$setPlayer(@NotNull Player player);

    void origins$clearPlayer();
}
