package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber
public class ActionOnBonemealPower extends Power {
    public static final MapCodec<ActionOnBonemealPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockAction.optionalCodec("block_action").forGetter(ActionOnBonemealPower::getBlockAction),
            EntityAction.optionalCodec("self_action").forGetter(ActionOnBonemealPower::getSelfAction),
            BlockCondition.optionalCodec("block_condition").forGetter(ActionOnBonemealPower::getBlockCondition)
    ).apply(instance, ActionOnBonemealPower::new));
    private final BlockAction blockAction;
    private final EntityAction selfAction;
    private final BlockCondition blockCondition;

    public ActionOnBonemealPower(BaseSettings settings, BlockAction blockAction, EntityAction selfAction, BlockCondition blockCondition) {
        super(settings);
        this.blockAction = blockAction;
        this.selfAction = selfAction;
        this.blockCondition = blockCondition;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public EntityAction getSelfAction() {
        return this.selfAction;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (event.getLevel().isClientSide) return;
        Player player = event.getPlayer();
        if (player == null) return;
        PowerHelper.get(player).execute(ActionOnBonemealPower.class,
                power -> power.blockCondition.test(event.getLevel(), event.getPos()),
                (holder, power) -> {
                    power.blockAction.execute(event.getLevel(), event.getPos(), Optional.empty());
                    power.selfAction.execute(player);
                });
    }
}
