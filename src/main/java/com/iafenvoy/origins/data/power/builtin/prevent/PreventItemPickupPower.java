package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreventItemPickupPower extends Power implements Prioritized {
    public static final MapCodec<PreventItemPickupPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(PreventItemPickupPower::getSettings),
            BiEntityAction.optionalCodec("bi_entity_action_thrower").forGetter(PreventItemPickupPower::getBiEntityActionThrower),
            BiEntityAction.optionalCodec("bi_entity_action_item").forGetter(PreventItemPickupPower::getBiEntityActionItem),
            ItemAction.optionalCodec("item_action").forGetter(PreventItemPickupPower::getItemAction),
            BiEntityCondition.optionalCodec("bi_entity_condition").forGetter(PreventItemPickupPower::getBiEntityCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemPickupPower::getItemCondition),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(PreventItemPickupPower::getPriority)
    ).apply(instance, PreventItemPickupPower::new));
    private final BiEntityAction biEntityActionThrower, biEntityActionItem;
    private final ItemAction itemAction;
    private final BiEntityCondition biEntityCondition;
    private final ItemCondition itemCondition;
    private final int priority;

    public PreventItemPickupPower(BaseSettings settings, BiEntityAction biEntityActionThrower, BiEntityAction biEntityActionItem, ItemAction itemAction, BiEntityCondition biEntityCondition, ItemCondition itemCondition, int priority) {
        super(settings);
        this.biEntityActionThrower = biEntityActionThrower;
        this.biEntityActionItem = biEntityActionItem;
        this.itemAction = itemAction;
        this.biEntityCondition = biEntityCondition;
        this.itemCondition = itemCondition;
        this.priority = priority;
    }

    public BiEntityAction getBiEntityActionThrower() {
        return this.biEntityActionThrower;
    }

    public BiEntityAction getBiEntityActionItem() {
        return this.biEntityActionItem;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public void executeActions(Entity entity, ItemEntity itemEntity, Entity thrower) {
        SlotAccess stackReference = SlotAccess.of(itemEntity::getItem, itemEntity::setItem);
        this.itemAction.execute(entity.level(), entity, stackReference);
        this.biEntityActionThrower.execute(thrower, entity);
        this.biEntityActionItem.execute(entity, itemEntity);
    }

    public static boolean doesPrevent(ItemEntity itemEntity, Entity entity) {
        ItemStack stack = itemEntity.getItem();
        Entity thrower = itemEntity.getOwner();
        if (thrower == null) return false;
        List<PreventItemPickupPower> powers = PowerHelper.get(entity).listActive(PreventItemPickupPower.class, p -> p.itemCondition.test(entity.level(), stack) && p.biEntityCondition.test(entity, thrower));
        powers.forEach(power -> power.executeActions(entity, itemEntity, thrower));
        return !powers.isEmpty();
    }
}
