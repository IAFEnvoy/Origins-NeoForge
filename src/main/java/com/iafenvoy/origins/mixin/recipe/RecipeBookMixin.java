package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

@Mixin(RecipeBook.class)
public abstract class RecipeBookMixin implements PowerCraftingObject {
    @Unique
    private WeakReference<Player> origins$player;

    @Nullable
    @Override
    public Player origins$getPlayer() {
        return this.origins$player == null ? null : this.origins$player.get();
    }

    @Override
    public void origins$setPlayer(Player player) {
        this.origins$player = new WeakReference<>(player);
    }
}
