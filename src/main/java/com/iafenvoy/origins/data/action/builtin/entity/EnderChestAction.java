package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;

public enum EnderChestAction implements EntityAction {
    INSTANCE;
    public static final MapCodec<EnderChestAction> CODEC = MapCodec.unit(INSTANCE);
    private static final Component TITLE = Component.translatable("container.enderchest");

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof Player player) {
            player.openMenu(new SimpleMenuProvider((syncId, inventory, p) -> ChestMenu.threeRows(syncId, inventory, p.getEnderChestInventory()), TITLE));
            player.awardStat(Stats.OPEN_ENDERCHEST);
        }
    }
}
