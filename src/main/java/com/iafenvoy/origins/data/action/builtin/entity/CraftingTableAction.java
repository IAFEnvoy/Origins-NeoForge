package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.accessor.ScreenHandlerUsabilityOverride;
import com.iafenvoy.origins.data.action.EntityAction;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;

public enum CraftingTableAction implements EntityAction {
    INSTANCE;
    public static final MapCodec<CraftingTableAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof Player player) {
            MenuConstructor handlerFactory = (syncId, playerInventory, _player) -> {
                CraftingMenu craftingScreenHandler = new CraftingMenu(syncId, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition()));
                ((ScreenHandlerUsabilityOverride) craftingScreenHandler).origins$canUse(true);
                return craftingScreenHandler;
            };
            player.openMenu(new SimpleMenuProvider(handlerFactory, Component.translatable("container.crafting")));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
    }
}
