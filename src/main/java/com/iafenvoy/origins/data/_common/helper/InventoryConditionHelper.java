package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.iafenvoy.origins.util.wrapper.ContainerWrapper;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public interface InventoryConditionHelper {
    Optional<Holder<Power>> power();

    ItemCondition itemCondition();

    IntList slot();

    default IntSet deduplicateSlots(Entity entity) {
        IntSet slots = new IntOpenHashSet(this.slot());
        int hotbarSlot = getDuplicatedSlotIndex(entity);
        if (hotbarSlot >= 0 && slots.contains(hotbarSlot))
            Optional.ofNullable(SlotRanges.nameToIds("weapon.mainhand")).map(SlotRange::slots).ifPresent(x -> x.forEach(slots::remove));
        return slots;
    }

    static int getDuplicatedSlotIndex(Entity entity) {
        if (entity instanceof Player player)
            return Optional.ofNullable(SlotRanges.nameToIds("hotbar." + player.getInventory().selected)).map(SlotRange::slots).map(List::getFirst).orElse(-1);
        return -1;
    }

    default ContainerWrapper getWrappedContainer(Entity entity) {
        return this.power().flatMap(power -> OriginDataHolder.get(entity).getComponentFor(power, InventoryComponent.class))
                .map(InventoryComponent::getContainer)
                .map(ContainerWrapper::container)
                .orElseGet(() -> ContainerWrapper.entity(entity));
    }

    default int checkInventory(Entity entity, Function<ItemStack, Integer> processor) {
        Set<Integer> slots = this.deduplicateSlots(entity);
        int matches = 0;
        ContainerWrapper container = this.getWrappedContainer(entity);
        for (int slot : slots) {
            SlotAccess access = container.get(slot);
            if (access == SlotAccess.NULL) continue;
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition().test(entity.level(), stack))
                matches += processor.apply(stack);
        }
        return matches;
    }

    enum ProcessMode implements StringRepresentable {
        STACKS(stack -> 1),
        ITEMS(ItemStack::getCount);
        public static final Codec<ProcessMode> CODEC = StringRepresentable.fromValues(ProcessMode::values);

        private final Function<ItemStack, Integer> processor;

        ProcessMode(Function<ItemStack, Integer> processor) {
            this.processor = processor;
        }

        public Function<ItemStack, Integer> getProcessor() {
            return this.processor;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
