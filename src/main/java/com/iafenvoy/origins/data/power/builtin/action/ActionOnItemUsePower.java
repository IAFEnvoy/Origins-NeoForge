package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Predicate;

@EventBusSubscriber
public class ActionOnItemUsePower extends Power implements Prioritized {
    public static final MapCodec<ActionOnItemUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnItemUsePower::getItemCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnItemUsePower::getEntityAction),
            ItemAction.optionalCodec("item_action").forGetter(ActionOnItemUsePower::getItemAction),
            Trigger.CODEC.optionalFieldOf("trigger", Trigger.FINISH).forGetter(ActionOnItemUsePower::getTrigger),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnItemUsePower::getPriority)
    ).apply(i, ActionOnItemUsePower::new));
    private final ItemCondition itemCondition;
    private final EntityAction entityAction;
    private final ItemAction itemAction;
    private final Trigger trigger;
    private final int priority;

    public ActionOnItemUsePower(BaseSettings settings, ItemCondition itemCondition, EntityAction entityAction, ItemAction itemAction, Trigger trigger, int priority) {
        super(settings);
        this.itemCondition = itemCondition;
        this.entityAction = entityAction;
        this.itemAction = itemAction;
        this.trigger = trigger;
        this.priority = priority;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public Trigger getTrigger() {
        return this.trigger;
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
    public static void onItemUse(LivingEntityUseItemEvent event) {
        OriginDataHolder.get(event.getEntity()).streamActivePowers(ActionOnItemUsePower.class).filter(x -> x.trigger.testEvent(event)).forEach(p -> {
            ItemStack stack = event.getItem();
            LivingEntity entity = event.getEntity();
            if (p.itemCondition.test(entity.level(), stack)) {
                p.entityAction.execute(entity);
                p.itemAction.execute(entity.level(), entity, event.getItem());
            }
        });
    }

    public enum Trigger implements StringRepresentable {
        INSTANT(e -> e instanceof LivingEntityUseItemEvent.Start start && start.getDuration() == 0),
        START(e -> e instanceof LivingEntityUseItemEvent.Start start && start.getDuration() > 0),
        DURING(e -> e instanceof LivingEntityUseItemEvent.Tick),
        STOP(e -> e instanceof LivingEntityUseItemEvent.Stop),
        FINISH(e -> e instanceof LivingEntityUseItemEvent.Finish);
        public static final Codec<Trigger> CODEC = StringRepresentable.fromValues(Trigger::values);

        private final Predicate<LivingEntityUseItemEvent> eventPredicate;

        Trigger(Predicate<LivingEntityUseItemEvent> eventPredicate) {
            this.eventPredicate = eventPredicate;
        }

        public boolean testEvent(LivingEntityUseItemEvent event) {
            return this.eventPredicate.test(event);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
