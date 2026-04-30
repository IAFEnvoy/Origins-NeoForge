package com.iafenvoy.origins.util;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.regular.InventoryPower;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

//FIXME::Rewrite & To helper
public class InventoryUtil {
    public static Set<Integer> getSlots(IntList slotArgumentTypes) {
        return new HashSet<>(slotArgumentTypes);
    }

    public static int checkInventory(ItemCondition itemCondition, IntList slotArgumentTypes, Entity entity, InventoryPower inventoryPower, Function<ItemStack, Integer> processor) {
        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);
        int matches = 0;

        if (inventoryPower == null) {
            for (int slot : slots) {

                SlotAccess slotAccess = entity.getSlot(slot);
                if (slotAccess == SlotAccess.NULL) {
                    continue;
                }

                ItemStack stack = slotAccess.get();
                if (!stack.isEmpty() && itemCondition.test(entity.level(), stack)) {
                    matches += processor.apply(stack);
                }
            }
        } else {
            Optional<InventoryComponent> optional = OriginDataHolder.get(entity).getComponentFor(inventoryPower, InventoryComponent.class);
            if (optional.isEmpty()) return 0;
            InventoryComponent component = optional.get();
            Container container = component.getContainer();

            for (int slot : slots) {

                if (slot < 0 || slot >= container.getContainerSize()) {
                    continue;
                }

                ItemStack stack = container.getItem(slot);
                if (!stack.isEmpty() && itemCondition.test(entity.level(), stack)) {
                    matches += processor.apply(stack);
                }
            }
        }

        return matches;
    }

    public static void modifyInventory(IntList slotArgumentTypes, EntityAction entityAction, ItemCondition itemCondition, ItemAction itemAction, Entity entity, Optional<Holder<Power>> powerId, Function<ItemStack, Integer> processor, int limit) {
        if (limit <= 0) {
            limit = Integer.MAX_VALUE;
        }

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);
        int counter = 0;

        if (powerId.isEmpty()) {
            for (int slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (!currentItemStack.isEmpty()) {
                        if (itemCondition.test(entity.level(), currentItemStack)) {
                            entityAction.execute(entity);
                            int amount = processor.apply(currentItemStack);
                            for (int i = 0; i < amount; i++) {
                                Mutable<ItemStack> newStack = Mutable.of(currentItemStack);
                                itemAction.execute(entity.level(), entity, newStack);
                                stackReference.set(newStack.get());

                                counter += 1;

                                if (counter >= limit) {
                                    break;
                                }
                            }

                            if (counter >= limit) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            Optional<InventoryComponent> optional = OriginDataHolder.get(entity).getComponentFor(powerId.get(), InventoryComponent.class);
            if (optional.isEmpty()) return;
            InventoryComponent component = optional.get();
            Container container = component.getContainer();
            int inventorySize = container.getContainerSize();
            slots.removeIf(slot -> slot > inventorySize);
            for (int i = 0; i < inventorySize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                ItemStack currentItemStack = container.getItem(i);
                if (!currentItemStack.isEmpty()) {
                    if (itemCondition.test(entity.level(), currentItemStack)) {
                        entityAction.execute(entity);
                        Mutable<ItemStack> newStack = Mutable.of(currentItemStack);
                        itemAction.execute(entity.level(), entity, newStack);
                        container.setItem(i, newStack.get());
                    }
                }
            }
        }

    }

    public static void replaceInventory(IntList slotArgumentTypes,
                                        ItemStack replacementStack,
                                        EntityAction entityAction,
                                        ItemCondition itemCondition,
                                        ItemAction itemAction,
                                        Entity entity, Optional<Holder<Power>> powerId,
                                        boolean mergeComponent) {

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);

        if (powerId.isEmpty()) {
            for (Integer slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (itemCondition.test(entity.level(), currentItemStack)) {
                        entityAction.execute(entity);
                        Mutable<ItemStack> newStack = Mutable.of(replacementStack.copy());
                        if (mergeComponent && !newStack.get().isComponentsPatchEmpty())
                            newStack.get().applyComponents(replacementStack.getComponents());
                        itemAction.execute(entity.level(), entity, newStack);
                        stackReference.set(newStack.get());
                    }
                }
            }
        } else {
            Optional<InventoryComponent> optional = OriginDataHolder.get(entity).getComponentFor(powerId.get(), InventoryComponent.class);
            if (optional.isEmpty()) return;
            InventoryComponent component = optional.get();
            Container container = component.getContainer();
            int inventorySize = container.getContainerSize();
            slots.removeIf(slot -> slot > inventorySize);
            for (int i = 0; i < inventorySize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                ItemStack currentItemStack = container.getItem(i);
                if (itemCondition.test(entity.level(), currentItemStack)) {
                    entityAction.execute(entity);
                    Mutable<ItemStack> newStack = Mutable.of(replacementStack.copy());
                    if (mergeComponent && !newStack.get().isComponentsPatchEmpty())
                        newStack.get().applyComponents(replacementStack.getComponents());
                    itemAction.execute(entity.level(), entity, newStack);
                    container.setItem(i, newStack.get());
                }
            }
        }

    }

    public static void dropInventory(IntList slotArgumentTypes, EntityAction entityAction, ItemCondition itemCondition, ItemAction itemAction, boolean throwRandomly, boolean retainOwnership, Entity entity, Optional<Holder<Power>> powerId, int amount) {

        Set<Integer> slots = getSlots(slotArgumentTypes);
        deduplicateSlots(entity, slots);

        if (powerId.isEmpty()) {
            for (Integer slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (!currentItemStack.isEmpty()) {
                        if (itemCondition.test(entity.level(), currentItemStack)) {
                            entityAction.execute(entity);
                            Mutable<ItemStack> newStack = Mutable.of(currentItemStack.copy());
                            itemAction.execute(entity.level(), entity, newStack);
                            if (amount != 0) {
                                int newAmount = amount > 0 ? amount * -1 : amount;
                                newStack.set(newStack.get().split(newAmount));
                                stackReference.set(newStack.get());
                            } else
                                stackReference.set(ItemStack.EMPTY);
                            throwItem(entity, newStack.get(), throwRandomly, retainOwnership);
                        }
                    }
                }
            }
        } else {
            Optional<InventoryComponent> optional = OriginDataHolder.get(entity).getComponentFor(powerId.get(), InventoryComponent.class);
            if (optional.isEmpty()) return;
            InventoryComponent component = optional.get();
            Container container = component.getContainer();
            int containerSize = container.getContainerSize();
            slots.removeIf(slot -> slot > containerSize);
            for (int i = 0; i < containerSize; i++) {
                if (!slots.isEmpty() && !slots.contains(i)) continue;
                ItemStack currentItemStack = container.getItem(i);
                if (!currentItemStack.isEmpty()) {
                    if (itemCondition.test(entity.level(), currentItemStack)) {
                        entityAction.execute(entity);
                        Mutable<ItemStack> newStack = Mutable.of(currentItemStack.copy());
                        itemAction.execute(entity.level(), entity, newStack);
                        if (amount != 0) {
                            int newAmount = amount > 0 ? amount * -1 : amount;
                            newStack.set(newStack.get().split(newAmount));
                            container.setItem(i, newStack.get());
                        } else
                            container.setItem(i, ItemStack.EMPTY);
                        throwItem(entity, newStack.get(), throwRandomly, retainOwnership);
                    }
                }
            }
        }

    }

    public static void throwItem(Entity thrower, ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {
        if (itemStack.isEmpty()) return;
        if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide)
            playerEntity.swing(InteractionHand.MAIN_HAND);

        double yOffset = thrower.getEyeY() - 0.3;
        ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
        itemEntity.setPickUpDelay(40);

        Random random = new Random();

        float f;
        float g;

        if (retainOwnership) itemEntity.setThrower(thrower);
        if (throwRandomly) {
            f = random.nextFloat() * 0.5F;
            g = random.nextFloat() * 6.2831855F;
            itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.2, Mth.cos(g) * f);
        } else {
            f = 0.3F;
            g = Mth.sin(thrower.getXRot() * 0.017453292F);
            float h = Mth.cos(thrower.getXRot() * 0.017453292F);
            float i = Mth.sin(thrower.getYRot() * 0.017453292F);
            float j = Mth.cos(thrower.getYRot() * 0.017453292F);
            float k = random.nextFloat() * 6.2831855F;
            float l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement(
                    (double) (-i * h * f) + Math.cos(k) * (double) l,
                    -g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F,
                    (double) (j * h * f) + Math.sin(k) * (double) l
            );
        }

        thrower.level().addFreshEntity(itemEntity);

    }

    /**
     * Use {@link InventoryUtil#forEachStack(Entity, Consumer)} instead.
     * The Mutable caused optimisation issues, and we will make the swap to SlotAccess
     * in future versions of the mod.
     *
     * @param entity            The entity to perform actions on the stacks of.
     * @param itemStackConsumer A consumer that decides what happens to the items,
     *                          DO NOT use the Mutable as it is null.
     */
    @Deprecated
    public static void forEachStack(Entity entity, BiConsumer<Mutable<ItemStack>, SlotAccess> itemStackConsumer) {
        forEachStack(entity, (slotAccess) -> itemStackConsumer.accept(null, slotAccess));
    }

    /*
    Includes the optimisations done in https://github.com/apace100/apoli/pull/132 prior to it releasing in Origins Fabric.
    There's no way I would've let the unoptimised version of this be present.

    We also provide the slot access in the BiConsumer, so we aren't setting the stack all the time.
     */
    public static void forEachStack(Entity entity, Consumer<SlotAccess> itemStackConsumer) {
        int skip = getDuplicatedSlotIndex(entity);

        for (int slot : SlotRanges.allNames().map(SlotRanges::nameToIds).filter(Objects::nonNull).map(SlotRange::slots).flatMap(IntCollection::stream).toList()) {
            if (slot == skip) {
                skip = Integer.MIN_VALUE;
                continue;
            }
            SlotAccess stackReference = entity.getSlot(slot);
            if (stackReference == SlotAccess.NULL) continue;

            ItemStack stack = stackReference.get();
            if (stack.isEmpty()) continue;
            itemStackConsumer.accept(stackReference);
        }

        OriginDataHolder holder = OriginDataHolder.get(entity);
        List<InventoryPower> inventoryPowers = holder.streamActivePowers(InventoryPower.class).toList();
        for (InventoryPower inventoryPower : inventoryPowers) {
            InventoryComponent inventory = holder.getComponentFor(inventoryPower, InventoryComponent.class).get();
            int inventorySize = inventory.getContainer().getContainerSize();
            for (int index = 0; index < inventorySize; index++) {
                ItemStack stack = inventory.getContainer().getItem(index);
                if (stack.isEmpty()) continue;
                itemStackConsumer.accept(SlotAccess.forContainer(inventory.getContainer(), index));
            }
        }
    }


    /*
    Includes the optimisations done in https://github.com/apace100/apoli/pull/132 prior to it releasing in Origins Fabric.
    There's no way I would've let the unoptimised version of this be present...
     */
    private static void deduplicateSlots(Entity entity, Set<Integer> slots) {
        int hotbarSlot = getDuplicatedSlotIndex(entity);
        if (hotbarSlot != Integer.MIN_VALUE && slots.contains(hotbarSlot))
            Optional.ofNullable(SlotRanges.nameToIds("weapon.mainhand")).map(SlotRange::slots).ifPresent(x -> x.forEach(slots::remove));
    }

    private static int getDuplicatedSlotIndex(Entity entity) {
        if (entity instanceof Player player) {
            int selectedSlot = player.getInventory().selected;
            return Optional.ofNullable(SlotRanges.nameToIds("hotbar." + selectedSlot)).map(SlotRange::slots).map(List::getFirst).orElse(Integer.MIN_VALUE);
        }
        return Integer.MIN_VALUE;
    }

    public enum ProcessMode implements StringRepresentable {
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
