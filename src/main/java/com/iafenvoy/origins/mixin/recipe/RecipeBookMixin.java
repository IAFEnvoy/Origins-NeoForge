package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.Objects;

@Mixin(RecipeBook.class)
public abstract class RecipeBookMixin implements PowerCraftingObject {
    @Unique
    private WeakReference<Player> origins$player;

    @Override
    public Player origins$getPlayer() {
        return Objects.requireNonNull(this.origins$player.get(), "Player was cleared; recipe book: " + this);
    }

    @Override
    public void origins$setPlayer(Player player) {
        this.origins$player = new WeakReference<>(player);
    }
}
