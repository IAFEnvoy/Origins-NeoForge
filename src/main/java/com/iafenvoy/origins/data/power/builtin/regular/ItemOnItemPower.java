package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.Mutable;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemOnItemPower extends Power {
    public static final MapCodec<ItemOnItemPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("using_item_condition").forGetter(ItemOnItemPower::getUsingItemCondition),
            ItemCondition.optionalCodec("on_item_condition").forGetter(ItemOnItemPower::getOnItemCondition),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(ItemOnItemPower::getResult),
            Codec.INT.optionalFieldOf("result_from_on_stack", 0).forGetter(ItemOnItemPower::getResultFromOnStack),
            ItemAction.optionalCodec("using_item_action").forGetter(ItemOnItemPower::getUsingItemAction),
            ItemAction.optionalCodec("on_item_action").forGetter(ItemOnItemPower::getOnItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(ItemOnItemPower::getResultItemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ItemOnItemPower::getEntityAction),
            ExtraEnumCodecs.CLICK_ACTION.optionalFieldOf("click_action", ClickAction.SECONDARY).forGetter(ItemOnItemPower::getClickAction)
    ).apply(i, ItemOnItemPower::new));
    private final ItemCondition usingItemCondition, onItemCondition;
    private final Optional<ItemStack> result;
    private final int resultFromOnStack;
    private final ItemAction usingItemAction, onItemAction, resultItemAction;
    private final EntityAction entityAction;
    private final ClickAction clickAction;

    public ItemOnItemPower(BaseSettings settings, ItemCondition usingItemCondition, ItemCondition onItemCondition, Optional<ItemStack> result, int resultFromOnStack, ItemAction usingItemAction, ItemAction onItemAction, ItemAction resultItemAction, EntityAction entityAction, ClickAction clickAction) {
        super(settings);
        this.usingItemCondition = usingItemCondition;
        this.onItemCondition = onItemCondition;
        this.result = result;
        this.resultFromOnStack = resultFromOnStack;
        this.usingItemAction = usingItemAction;
        this.onItemAction = onItemAction;
        this.resultItemAction = resultItemAction;
        this.entityAction = entityAction;
        this.clickAction = clickAction;
    }

    public ItemCondition getUsingItemCondition() {
        return this.usingItemCondition;
    }

    public ItemCondition getOnItemCondition() {
        return this.onItemCondition;
    }

    public Optional<ItemStack> getResult() {
        return this.result;
    }

    public int getResultFromOnStack() {
        return this.resultFromOnStack;
    }

    public ItemAction getUsingItemAction() {
        return this.usingItemAction;
    }

    public ItemAction getOnItemAction() {
        return this.onItemAction;
    }

    public ItemAction getResultItemAction() {
        return this.resultItemAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public ClickAction getClickAction() {
        return this.clickAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    //FIXME::Optimize
    public static boolean execute(Entity entity, Slot self, SlotAccess other, ClickAction action) {
        List<ItemOnItemPower> powers = OriginDataHolder.get(entity).streamActivePowers(ItemOnItemPower.class)
                .filter(p -> p.clickAction == action && p.usingItemCondition.test(entity.level(), other.get()) && p.onItemCondition.test(entity.level(), self.getItem()))
                .toList();
        powers.forEach(p -> p.apply(entity, self, other));
        return !powers.isEmpty();
    }

    public void apply(Entity entity, Slot self, SlotAccess other) {
        this.execute(entity, Mutable.access(other::get, other::set), Mutable.access(self::getItem, self::set), self);
    }

    public void execute(Entity entity, Mutable<ItemStack> using, Mutable<ItemStack> on, Slot slot) {
        Mutable<ItemStack> stack = Mutable.of(ItemStack.EMPTY);
        if (this.result.isPresent()) stack.set(this.result.get().copy());
        else if (this.resultFromOnStack > 0) stack.set(on.get().split(this.resultFromOnStack));
        else stack.set(on.get());
        this.resultItemAction.execute(entity.level(), entity, stack);
        this.usingItemAction.execute(entity.level(), entity, using);
        this.onItemAction.execute(entity.level(), entity, on);
        if (this.result.isPresent()) {
            if (slot.getItem().isEmpty())
                slot.set(stack.get());
            else if (entity instanceof Player player)
                player.getInventory().placeItemBackInInventory(stack.get());
        }
        this.entityAction.execute(entity);
    }
}
