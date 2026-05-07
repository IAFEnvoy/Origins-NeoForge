package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.data.power.Power;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.LinkedList;

@Mixin(TransientCraftingContainer.class)
public abstract class TransientCraftingContainerMixin implements PowerCraftingInventory {
    @Unique
    private Collection<? extends Power> origins$CachedPowerTypes = new LinkedList<>();
    @Unique
    private Player origins$cachedPlayer;

    @Override
    public Collection<? extends Power> origins$getPowerTypes() {
        return this.origins$CachedPowerTypes;
    }

    @Override
    public void origins$setPowerTypes(Collection<? extends Power> powerTypes) {
        this.origins$CachedPowerTypes = powerTypes;
    }

    @Override
    public TransientCraftingContainer origins$getInventory() {
        return (TransientCraftingContainer) (Object) this;
    }

    @Override
    public Player origins$getPlayer() {
        return this.origins$cachedPlayer;
    }

    @Override
    public void origins$setPlayer(Player player) {
        this.origins$cachedPlayer = player;
    }
}
