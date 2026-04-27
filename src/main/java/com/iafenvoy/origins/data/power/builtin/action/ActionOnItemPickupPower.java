package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnItemPickupPower extends Power implements Prioritized {
    public static final MapCodec<ActionOnItemPickupPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(ActionOnItemPickupPower::getSettings),
            BiEntityAction.optionalCodec("bi_entity_action").forGetter(ActionOnItemPickupPower::getBiEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnItemPickupPower::getItemAction),
            BiEntityCondition.optionalCodec("bi_entity_condition").forGetter(ActionOnItemPickupPower::getBiEntityCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnItemPickupPower::getItemCondition),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnItemPickupPower::getPriority)
    ).apply(i, ActionOnItemPickupPower::new));
    private final BiEntityAction biEntityAction;
    private final ItemAction itemAction;
    private final BiEntityCondition biEntityCondition;
    private final ItemCondition itemCondition;
    private final int priority;

    public ActionOnItemPickupPower(BaseSettings settings, BiEntityAction biEntityAction, ItemAction itemAction, BiEntityCondition biEntityCondition, ItemCondition itemCondition, int priority) {
        super(settings);
        this.biEntityAction = biEntityAction;
        this.itemAction = itemAction;
        this.biEntityCondition = biEntityCondition;
        this.itemCondition = itemCondition;
        this.priority = priority;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
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

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent event) {
        Entity actor = event.getItemEntity().getOwner(), target = event.getPlayer();
        Level level = target.level();
        ItemStack stack = event.getItemEntity().getItem();
        if (actor == null) return;
        OriginDataHolder.get(target).executePowersWithCondition(ActionOnItemPickupPower.class,
                p -> p.biEntityCondition.test(actor, target) && p.itemCondition.test(level, stack),
                p -> {
                    p.biEntityAction.execute(actor, target);
                    p.itemAction.execute(level, target, stack);
                });
    }
}
