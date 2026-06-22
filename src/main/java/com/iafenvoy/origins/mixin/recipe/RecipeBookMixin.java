package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

@Mixin(RecipeBook.class)
public abstract class RecipeBookMixin implements PowerCraftingObject {
    @Unique
    private WeakReference<Player> origins$player;

    @Override
    public Optional<Player> origins$getPlayer() {
        return Optional.ofNullable(this.origins$player).map(Reference::get);
    }

    @Override
    public void origins$setPlayer(@NotNull Player player) {
        this.origins$player = new WeakReference<>(player);
    }

    @Override
    public void origins$clearPlayer() {
        this.origins$player = null;
    }
}
