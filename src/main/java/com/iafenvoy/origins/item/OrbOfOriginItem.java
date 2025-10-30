package com.iafenvoy.origins.item;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.network.LoginHelper;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrbOfOriginItem extends Item {
    public OrbOfOriginItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE).component(OriginsDataComponents.ORB_LAYERS, List.of()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            List<Holder<Layer>> layers = stack.getOrDefault(OriginsDataComponents.ORB_LAYERS, List.of());
            if (layers.isEmpty()) LoginHelper.openGuiForLayer(serverPlayer, null);
            else for (Holder<Layer> layer : layers) LoginHelper.openGuiForLayer(serverPlayer, layer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
