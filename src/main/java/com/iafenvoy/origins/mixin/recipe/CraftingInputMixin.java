package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.data.power.Power;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.LinkedList;

@Mixin(CraftingInput.class)
public abstract class CraftingInputMixin implements PowerCraftingInventory {
    @Unique
    private Collection<? extends Power> origins$cachedPowerTypes = new LinkedList<>();
    @Unique
    private Player origins$cachedPlayer;
    @Unique
    private TransientCraftingContainer origins$inventory;

    @Override
    public Collection<? extends Power> origins$getPowerTypes() {
        return this.origins$cachedPowerTypes;
    }

    @Override
    public void origins$setPowerTypes(Collection<? extends Power> powerType) {
        this.origins$cachedPowerTypes = powerType;
        if (this.origins$getInventory() instanceof PowerCraftingInventory pci)
            pci.origins$setPowerTypes(this.origins$getPowerTypes());
    }

    @Override
    public Player origins$getPlayer() {
        return this.origins$cachedPlayer;
    }

    @Override
    public void origins$setPlayer(Player player) {
        this.origins$cachedPlayer = player;
        if (this.origins$getInventory() instanceof PowerCraftingInventory pci)
            pci.origins$setPlayer(this.origins$getPlayer());
    }

    @Override
    public TransientCraftingContainer origins$getInventory() {
        return this.origins$inventory;
    }

    @Override
    public void origins$setInventory(TransientCraftingContainer inventory) {
        this.origins$inventory = inventory;
    }
}
