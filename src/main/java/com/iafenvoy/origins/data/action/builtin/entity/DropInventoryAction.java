package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.builtin.regular.InventoryPower;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public record DropInventoryAction(EntityAction entityAction, ItemAction itemAction, ItemCondition itemCondition,
                                  List<Integer> slots, Optional<ResourceLocation> power, boolean throwRandomly,
                                  boolean retainOwnership, int amount) implements EntityAction {
    public static final MapCodec<DropInventoryAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAction.optionalCodec("entity_action").forGetter(DropInventoryAction::entityAction),
            ItemAction.optionalCodec("item_action").forGetter(DropInventoryAction::itemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(DropInventoryAction::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", List.of()).forGetter(DropInventoryAction::slots),
            ResourceLocation.CODEC.optionalFieldOf("power").forGetter(DropInventoryAction::power),
            Codec.BOOL.optionalFieldOf("throw_randomly", false).forGetter(DropInventoryAction::throwRandomly),
            Codec.BOOL.optionalFieldOf("retain_ownership", true).forGetter(DropInventoryAction::retainOwnership),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(DropInventoryAction::amount)
    ).apply(instance, DropInventoryAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        dropInventory(this.slots(), this.entityAction(), this.itemCondition(), this.itemAction(), this.throwRandomly(), this.retainOwnership(), source, this.power(), this.amount());
    }

    //TODO::Power Inventory & Optimize Code
    public static void dropInventory(List<Integer> slots, EntityAction entityAction, ItemCondition itemCondition, ItemAction itemAction, boolean throwRandomly, boolean retainOwnership, Entity entity, Optional<ResourceLocation> powerId, int amount) {
        if (powerId.isEmpty()) {
            for (Integer slot : slots) {
                SlotAccess stackReference = entity.getSlot(slot);
                if (stackReference != SlotAccess.NULL) {
                    ItemStack currentItemStack = stackReference.get();
                    if (!currentItemStack.isEmpty()) {
                        if (itemCondition.test(entity.level(), currentItemStack)) {
                            entityAction.execute(entity);
                            ItemStack newStack = currentItemStack.copy();
                            itemAction.execute(entity.level(), entity, newStack);
                            if (amount != 0) {
                                int newAmount = amount > 0 ? amount * -1 : amount;
                                newStack.split(newAmount);
                                stackReference.set(newStack);
                            } else stackReference.set(ItemStack.EMPTY);
                            throwItem(entity, newStack, throwRandomly, retainOwnership);
                        }
                    }
                }
            }
        } else {
//            ConfiguredPower<?, ?> power = PowerContainer.get(entity).resolve()
//                    .map(x -> x.getPower(powerId.get())).map(Holder::value).orElse(null);
//            if (power == null || !(power.getFactory() instanceof InventoryPower)) return;
//            ConfiguredPower<InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<InventoryConfiguration, InventoryPower>) power;
//            int containerSize = inventoryPower.getFactory().getSize(inventoryPower, entity);
//            slots.removeIf(slot -> slot > containerSize);
//            for (int i = 0; i < containerSize; i++) {
//                if (!slots.isEmpty() && !slots.contains(i)) continue;
//                Container container = inventoryPower.getFactory().getInventory(inventoryPower, entity);
//                ItemStack currentItemStack = container.getItem(i);
//                if (!currentItemStack.isEmpty()) {
//                    if (ConfiguredItemCondition.check(itemCondition, entity.level(), currentItemStack)) {
//                        ConfiguredEntityAction.execute(entityAction, entity);
//                        Mutable<ItemStack> newStack = new MutableObject<>(currentItemStack.copy());
//                        ConfiguredItemAction.execute(itemAction, entity.level(), newStack);
//                        if (amount != 0) {
//                            int newAmount = amount > 0 ? amount * -1 : amount;
//                            newStack.setValue(newStack.getValue().split(newAmount));
//                            container.setItem(i, newStack.getValue());
//                        } else
//                            container.setItem(i, ItemStack.EMPTY);
//                        throwItem(entity, newStack.getValue(), throwRandomly, retainOwnership);
//                    }
//                }
//            }
        }

    }

    public static void throwItem(Entity thrower, ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {

        if (itemStack.isEmpty()) return;
        if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide)
            playerEntity.swing(InteractionHand.MAIN_HAND);

        double yOffset = thrower.getEyeY() - 0.30000001192092896D;
        ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
        itemEntity.setPickUpDelay(40);

        Random random = new Random();

        float f;
        float g;

        if (retainOwnership) itemEntity.setThrower(thrower);
        if (throwRandomly) {
            f = random.nextFloat() * 0.5F;
            g = random.nextFloat() * 6.2831855F;
            itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.20000000298023224D, Mth.cos(g) * f);
        } else {
            f = 0.3F;
            g = Mth.sin(thrower.getXRot() * 0.017453292F);
            float h = Mth.cos(thrower.getXRot() * 0.017453292F);
            float i = Mth.sin(thrower.getYRot() * 0.017453292F);
            float j = Mth.cos(thrower.getYRot() * 0.017453292F);
            float k = random.nextFloat() * 6.2831855F;
            float l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement((double) (-i * h * f) + Math.cos(k) * (double) l, -g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, (double) (j * h * f) + Math.sin(k) * (double) l);
        }

        thrower.level().addFreshEntity(itemEntity);
    }
}
