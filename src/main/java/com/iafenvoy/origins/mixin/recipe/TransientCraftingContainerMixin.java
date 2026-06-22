package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.data.power.Power;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Mixin(TransientCraftingContainer.class)
public abstract class TransientCraftingContainerMixin implements PowerCraftingInventory {
    @Unique
    private Collection<? extends Power> origins$CachedPowerTypes = new LinkedList<>();
    @Unique
    @Nullable
    private Player origins$cachedPlayer;

    @Override
    public Collection<? extends Power> origins$getPowerTypes() {
        return this.origins$CachedPowerTypes;
    }

    @Override
    public void origins$setPowerTypes(Collection<? extends Power> powerTypes) {
        this.origins$CachedPowerTypes = powerTypes;
    }

    @Nullable
    @Override
    public TransientCraftingContainer origins$getInventory() {
        return (TransientCraftingContainer) (Object) this;
    }

    @Override
    public Optional<Player> origins$getPlayer() {
        return Optional.ofNullable(this.origins$cachedPlayer);
    }

    @Override
    public void origins$setPlayer(@NotNull Player player) {
        this.origins$cachedPlayer = player;
    }

    @Override
    public void origins$clearPlayer() {
        this.origins$cachedPlayer = null;
    }
}
