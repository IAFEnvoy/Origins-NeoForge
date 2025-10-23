package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

public enum CraftingTableAction implements EntityAction {
    INSTANCE;
    public static final MapCodec<CraftingTableAction> CODEC = MapCodec.unit(INSTANCE);
    private static final Component TITLE = Component.translatable("container.crafting");

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        if (source instanceof Player player) {
            player.openMenu(new SimpleMenuProvider((syncId, inventory, o) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(o.level(), o.blockPosition())), TITLE));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
    }
}
