package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GiveItemAction(ItemStack stack, ItemAction itemAction,
                             Optional<EquipmentSlot> preferredSlot) implements EntityAction {
    public static final MapCodec<GiveItemAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(GiveItemAction::stack),
            ItemAction.optionalCodec("item_action").forGetter(GiveItemAction::itemAction),
            EquipmentSlot.CODEC.optionalFieldOf("preferred_slot").forGetter(GiveItemAction::preferredSlot)
    ).apply(i, GiveItemAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (source instanceof Player player) {
            ItemStack stack = this.stack.copy();
            this.itemAction.execute(player.level(), source, stack);
            if (this.preferredSlot.isPresent() && player.getItemBySlot(this.preferredSlot.get()).isEmpty())
                player.setItemSlot(this.preferredSlot.get(), stack);
            else player.getInventory().placeItemBackInInventory(stack);
        }

    }
}
