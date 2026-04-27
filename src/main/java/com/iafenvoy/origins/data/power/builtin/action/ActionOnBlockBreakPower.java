package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnBlockBreakPower extends Power {
    public static final MapCodec<ActionOnBlockBreakPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnBlockBreakPower::getBlockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnBlockBreakPower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnBlockBreakPower::getBlockAction),
            Codec.BOOL.optionalFieldOf("only_when_success", true).forGetter(ActionOnBlockBreakPower::isOnlyWheSuccess)
    ).apply(i, ActionOnBlockBreakPower::new));
    private final BlockCondition blockCondition;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final boolean onlyWheSuccess;

    public ActionOnBlockBreakPower(BaseSettings settings, BlockCondition blockCondition, EntityAction entityAction, BlockAction blockAction, boolean onlyWheSuccess) {
        super(settings);
        this.blockCondition = blockCondition;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.onlyWheSuccess = onlyWheSuccess;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public boolean isOnlyWheSuccess() {
        return this.onlyWheSuccess;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        OriginDataHolder.get(event.getPlayer()).streamActivePowers(ActionOnBlockBreakPower.class).forEach(power -> {
            if (power.onlyWheSuccess && event.isCanceled()) return;
            if (!power.blockCondition.test(event.getPlayer().level(), event.getPos())) return;
            power.entityAction.execute(event.getPlayer());
            power.blockAction.execute(event.getPlayer().level(), event.getPos(), event.getPlayer().getDirection());
        });
    }
}
