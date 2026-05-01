package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.wrapper.ContainerWrapper;
import com.iafenvoy.origins.util.wrapper.Mutable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public interface InventoryActionHelper extends InventoryConditionHelper {
    EntityAction entityAction();

    ItemAction itemAction();

    default void modifyInventory(Entity entity, Function<ItemStack, Integer> processor, int limit) {
        if (limit <= 0) limit = Integer.MAX_VALUE;
        Set<Integer> slots = this.deduplicateSlots(entity);
        int counter = 0;
        ContainerWrapper container = this.getWrappedContainer(entity);
        for (int slot : slots) {
            SlotAccess access = container.get(slot);
            if (access == SlotAccess.NULL) continue;
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition().test(entity.level(), stack)) {
                this.entityAction().execute(entity);
                int amount = processor.apply(stack);
                for (int i = 0; i < amount; i++) {
                    Mutable<ItemStack> newStack = Mutable.of(stack);
                    this.itemAction().execute(entity.level(), entity, newStack);
                    access.set(newStack.get());
                    counter++;
                    if (counter >= limit) break;
                }
                if (counter >= limit) break;
            }
        }
    }

    default void replaceInventory(ItemStack replacementStack, Entity entity, boolean mergeComponent) {
        Set<Integer> slots = this.deduplicateSlots(entity);
        ContainerWrapper container = this.getWrappedContainer(entity);
        for (Integer slot : slots) {
            SlotAccess access = container.get(slot);
            if (access == SlotAccess.NULL) continue;
            ItemStack stack = access.get();
            if (this.itemCondition().test(entity.level(), stack)) {
                this.entityAction().execute(entity);
                Mutable<ItemStack> newStack = Mutable.of(replacementStack.copy());
                if (mergeComponent && !newStack.get().isComponentsPatchEmpty())
                    newStack.get().applyComponents(replacementStack.getComponents());
                this.itemAction().execute(entity.level(), entity, newStack);
                access.set(newStack.get());
            }
        }
    }

    default void dropInventory(boolean throwRandomly, boolean retainOwnership, Entity entity, int amount) {
        Set<Integer> slots = this.deduplicateSlots(entity);
        ContainerWrapper container = this.getWrappedContainer(entity);
        for (Integer slot : slots) {
            SlotAccess access = container.get(slot);
            if (access == SlotAccess.NULL) continue;
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition().test(entity.level(), stack)) {
                this.entityAction().execute(entity);
                Mutable<ItemStack> newStack = Mutable.of(stack.copy());
                this.itemAction().execute(entity.level(), entity, newStack);
                if (amount != 0) {
                    int newAmount = amount > 0 ? amount * -1 : amount;
                    newStack.set(newStack.get().split(newAmount));
                    access.set(newStack.get());
                } else access.set(ItemStack.EMPTY);
                this.throwItem(entity, newStack.get(), throwRandomly, retainOwnership);
            }
        }
    }

    default void throwItem(Entity thrower, ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        if (stack.isEmpty()) return;
        if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide)
            playerEntity.swing(InteractionHand.MAIN_HAND);

        double yOffset = thrower.getEyeY() - 0.3;
        ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), stack);
        itemEntity.setPickUpDelay(40);

        Random random = new Random();
        if (retainOwnership) itemEntity.setThrower(thrower);
        if (throwRandomly) {
            double f = random.nextFloat() * 0.5F, g = random.nextFloat() * 2 * Math.PI;
            itemEntity.setDeltaMovement(-Math.sin(g) * f, 0.2, Math.cos(g) * f);
        } else {
            double f = 0.3F;
            double g = Math.sin(Math.toRadians(thrower.getXRot()));
            double h = Math.cos(Math.toRadians(thrower.getXRot()));
            double i = Math.sin(Math.toRadians(thrower.getYRot()));
            double j = Math.cos(Math.toRadians(thrower.getYRot()));
            double k = random.nextFloat() * Math.PI, l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement(-i * h * f + Math.cos(k) * l, -g * f + (1 + random.nextFloat() - random.nextFloat()) * 0.1, j * h * f + Math.sin(k) * l);
        }
        thrower.level().addFreshEntity(itemEntity);
    }
}
