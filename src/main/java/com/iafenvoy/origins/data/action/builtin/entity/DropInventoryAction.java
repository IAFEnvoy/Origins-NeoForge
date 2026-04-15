package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.iafenvoy.origins.util.Mutable;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public record DropInventoryAction(Optional<ResourceLocation> power, EntityAction entityAction, ItemAction itemAction,
                                  ItemCondition itemCondition, List<Integer> slot, boolean throwRandomly,
                                  boolean retainOwnership, int amount) implements EntityAction {
    public static final MapCodec<DropInventoryAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("power").forGetter(DropInventoryAction::power),
            EntityAction.optionalCodec("entity_action").forGetter(DropInventoryAction::entityAction),
            ItemAction.optionalCodec("item_action").forGetter(DropInventoryAction::itemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(DropInventoryAction::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", List.of()).forGetter(DropInventoryAction::slot),
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
        OriginDataHolder holder = OriginDataHolder.get(source);
        Int2ObjectFunction<SlotAccess> access = this.power.map(id -> holder.getComponent(id, InventoryComponent.class))
                .flatMap(Function.identity())
                .map(InventoryComponent::container)
                .<Int2ObjectFunction<SlotAccess>>map(c -> id -> SlotAccess.forContainer(c, id))
                .orElse(source::getSlot);
        for (Integer slot1 : this.slot) {
            SlotAccess stackReference = access.apply(slot1);
            if (stackReference != SlotAccess.NULL) {
                ItemStack stack = stackReference.get();
                if (!stack.isEmpty()) {
                    if (this.itemCondition.test(source.level(), stack)) {
                        this.entityAction.execute(source);
                        Mutable<ItemStack> newStack = new Mutable<>(stack.copy());
                        this.itemAction.execute(source.level(), source, newStack);
                        if (this.amount != 0) {
                            int newAmount = this.amount > 0 ? this.amount * -1 : this.amount;
                            newStack.set(newStack.get().split(newAmount));
                            stackReference.set(newStack.get());
                        } else stackReference.set(ItemStack.EMPTY);
                        this.throwItem(source, newStack.get());
                    }
                }
            }
        }
    }

    public void throwItem(Entity thrower, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide)
            playerEntity.swing(InteractionHand.MAIN_HAND);

        double yOffset = thrower.getEyeY() - 0.3;
        ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), stack);
        itemEntity.setPickUpDelay(40);

        Random random = new Random();

        if (this.retainOwnership) itemEntity.setThrower(thrower);
        if (this.throwRandomly) {
            float f = random.nextFloat() * 0.5F;
            float g = random.nextFloat() * 2 * (float) Math.PI;
            itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.2, Mth.cos(g) * f);
        } else {
            float f = 0.3F;
            float g = Mth.sin((float) Math.toRadians(thrower.getXRot()));
            float h = Mth.cos((float) Math.toRadians(thrower.getXRot()));
            float i = Mth.sin((float) Math.toRadians(thrower.getYRot()));
            float j = Mth.cos((float) Math.toRadians(thrower.getYRot()));
            float k = random.nextFloat() * 6.2831855F;
            float l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement((double) (-i * h * f) + Math.cos(k) * (double) l, -g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, (double) (j * h * f) + Math.sin(k) * (double) l);
        }

        thrower.level().addFreshEntity(itemEntity);
    }
}
