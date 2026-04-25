package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber
public class ActionOnWakeUpPower extends Power {
    public static final MapCodec<ActionOnWakeUpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnWakeUpPower::getBlockCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnWakeUpPower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnWakeUpPower::getBlockAction)
    ).apply(i, ActionOnWakeUpPower::new));
    private final BlockCondition blockCondition;
    private final EntityAction entityAction;
    private final BlockAction blockAction;

    public ActionOnWakeUpPower(BaseSettings settings, BlockCondition blockCondition, EntityAction entityAction, BlockAction blockAction) {
        super(settings);
        this.blockCondition = blockCondition;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
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

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onWakeup(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Optional<BlockPos> pos = player.getSleepingPos();
        if (pos.isEmpty()) return;
        OriginDataHolder.get(player).streamActivePowers(ActionOnWakeUpPower.class).forEach(power -> {
            if (power.getBlockCondition().test(player.level(), pos.get())) {
                power.getEntityAction().execute(player);
                power.getBlockAction().execute(player.level(), pos.get(), player.getDirection());
            }
        });

    }
}