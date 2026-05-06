package com.iafenvoy.origins.accessor;

import com.iafenvoy.origins.data.power.Power;
import net.minecraft.world.inventory.TransientCraftingContainer;

import java.util.Collection;

public interface PowerCraftingInventory extends PowerCraftingObject {
    Collection<? extends Power> origins$getPowerTypes();

    void origins$setPowerTypes(Collection<? extends Power> powerType);

    default TransientCraftingContainer origins$getInventory() {
        return null;
    }

    default void origins$setInventory(TransientCraftingContainer inventory) {
    }
}
