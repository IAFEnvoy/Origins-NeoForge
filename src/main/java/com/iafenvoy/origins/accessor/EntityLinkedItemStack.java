package com.iafenvoy.origins.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface EntityLinkedItemStack {
    Entity origins$getEntity();

    Entity origins$getEntity(boolean prioritiseVanillaHolder);

    void origins$setEntity(Entity entity);

    /**
     * Safe cast helper - extracts entity from an ItemStack via the mixin interface.
     * Returns null if the entity could not be retrieved.
     */
    @Nullable
    static Entity getEntity(ItemStack stack) {
        return ((EntityLinkedItemStack) (Object) stack).origins$getEntity();
    }
}
