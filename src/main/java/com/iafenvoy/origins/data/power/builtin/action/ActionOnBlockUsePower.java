package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data._common.ActionInteractionSettings;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.MiscUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class ActionOnBlockUsePower extends Power {
    public static final MapCodec<ActionOnBlockUsePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(ActionOnBlockUsePower::getSettings),
            ActionInteractionSettings.CODEC.forGetter(ActionOnBlockUsePower::getInteractionSettings),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnBlockUsePower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnBlockUsePower::getBlockAction),
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnBlockUsePower::getBlockCondition),
            Direction.CODEC.listOf().optionalFieldOf("directions", List.of(Direction.values())).forGetter(ActionOnBlockUsePower::getDirections)
    ).apply(i, ActionOnBlockUsePower::new));
    private final ActionInteractionSettings interactionSettings;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final BlockCondition blockCondition;
    private final List<Direction> directions;

    public ActionOnBlockUsePower(BaseSettings settings, ActionInteractionSettings interactionSettings, EntityAction entityAction, BlockAction blockAction, BlockCondition blockCondition, List<Direction> directions) {
        super(settings);
        this.interactionSettings = interactionSettings;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.blockCondition = blockCondition;
        this.directions = directions;
    }

    public ActionInteractionSettings getInteractionSettings() {
        return this.interactionSettings;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public List<Direction> getDirections() {
        return this.directions;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public Optional<InteractionResult> tryExecute(Entity entity, BlockPos pos, Direction direction, InteractionHand hand) {
        if (entity instanceof LivingEntity living && this.check(entity.level(), pos, direction, hand, living.getItemInHand(hand)))
            return Optional.of(this.executeAction(entity, pos, direction, hand));
        return Optional.empty();
    }

    public boolean check(Level level, BlockPos blockPos, Direction direction, InteractionHand hand, ItemStack heldStack) {
        if (!this.interactionSettings.appliesTo(level, hand, heldStack)) return false;
        if (!this.directions.contains(direction)) return false;
        return this.blockCondition.test(level, blockPos);
    }

    public InteractionResult executeAction(Entity entity, BlockPos blockPos, Direction direction, InteractionHand hand) {
        this.blockAction.execute(entity.level(), blockPos, direction);
        this.entityAction.execute(entity);
        if (entity instanceof LivingEntity living) this.interactionSettings.performActorItemStuff(living, hand);
        return this.interactionSettings.actionResult();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        Entity entity = event.getEntity();
        OriginDataHolder.get(entity).streamActivePowers(ActionOnBlockUsePower.class)
                .flatMap(x -> x.tryExecute(entity, event.getPos(), event.getFace(), event.getHand()).stream())
                .reduce(MiscUtil::reduce).ifPresent(res -> {
                    if (res.consumesAction()) {
                        event.setCancellationResult(res);
                        event.setCanceled(true);
                    }
                });
    }
}
