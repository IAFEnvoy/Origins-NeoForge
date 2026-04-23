package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.data.power.builtin.ActionPowers;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class ActionOnBeingUsedPower extends Power implements Prioritized {
    public static final MapCodec<ActionOnBeingUsedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ActionOnBeingUsedPower::getBiEntityAction),
            ItemAction.optionalCodec("held_item_action").forGetter(ActionOnBeingUsedPower::getHeldItemAction),
            ItemAction.optionalCodec("result_item_action").forGetter(ActionOnBeingUsedPower::getResultItemAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnBeingUsedPower::getBiEntityCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(ActionOnBeingUsedPower::getItemCondition),
            CombinedCodecs.HAND.optionalFieldOf("hands", List.of(InteractionHand.values())).forGetter(ActionOnBeingUsedPower::getHands),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(ActionOnBeingUsedPower::getResultStack),
            ExtraEnumCodecs.INTERACTION_RESULT.optionalFieldOf("interaction_result", InteractionResult.SUCCESS).forGetter(ActionOnBeingUsedPower::getInteractionResult),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ActionOnBeingUsedPower::priority)
    ).apply(i, ActionOnBeingUsedPower::new));
    private final BiEntityAction biEntityAction;
    private final ItemAction heldItemAction;
    private final ItemAction resultItemAction;
    private final BiEntityCondition biEntityCondition;
    private final ItemCondition itemCondition;
    private final List<InteractionHand> hands;
    private final Optional<ItemStack> resultStack;
    private final InteractionResult interactionResult;
    private final int priority;

    public ActionOnBeingUsedPower(BaseSettings settings, BiEntityAction biEntityAction, ItemAction heldItemAction, ItemAction resultItemAction, BiEntityCondition biEntityCondition, ItemCondition itemCondition, List<InteractionHand> hands, Optional<ItemStack> resultStack, InteractionResult interactionResult, int priority) {
        super(settings);
        this.biEntityAction = biEntityAction;
        this.heldItemAction = heldItemAction;
        this.resultItemAction = resultItemAction;
        this.biEntityCondition = biEntityCondition;
        this.itemCondition = itemCondition;
        this.hands = hands;
        this.resultStack = resultStack;
        this.interactionResult = interactionResult;
        this.priority = priority;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public ItemAction getHeldItemAction() {
        return this.heldItemAction;
    }

    public ItemAction getResultItemAction() {
        return this.resultItemAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public List<InteractionHand> getHands() {
        return this.hands;
    }

    public Optional<ItemStack> getResultStack() {
        return this.resultStack;
    }

    public InteractionResult getInteractionResult() {
        return this.interactionResult;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onBeingUsed(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity entity = event.getTarget();
        Level level = player.level();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        for (ActionOnBeingUsedPower power : OriginDataHolder.get(player).getPowers(ActionPowers.ACTION_ON_BEING_USED, ActionOnBeingUsedPower.class)) {
            if (power.getHands().contains(hand) && power.getBiEntityCondition().test(player, entity) && power.getItemCondition().test(level, stack)) {
                power.getBiEntityAction().execute(player, entity);
                power.getHeldItemAction().execute(level, player, stack);
                if (power.getResultStack().isPresent()) {
                    ItemStack result = power.getResultStack().get().copy();
                    power.getResultItemAction().execute(level, player, result);
                    if (stack.isEmpty()) player.setItemInHand(hand, result);
                    else if (!player.getInventory().add(result)) player.drop(result, false);
                }
                event.setCancellationResult(power.getInteractionResult());
                event.setCanceled(true);
                return;
            }
        }
    }
}
