package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class PreventSleepPower extends Power implements Prioritized {
    public static final MapCodec<PreventSleepPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(PreventSleepPower::getBlockCondition),
            ComponentSerialization.CODEC.optionalFieldOf("message", Component.translatable("text.origins.cannot_sleep")).forGetter(PreventSleepPower::getMessage),
            Codec.BOOL.optionalFieldOf("set_spawn_point", false).forGetter(PreventSleepPower::shouldSetSpawnPoint),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(PreventSleepPower::getPriority)
    ).apply(i, PreventSleepPower::new));
    private final BlockCondition blockCondition;
    private final Component message;
    private final boolean setSpawnPoint;
    private final int priority;

    public PreventSleepPower(BaseSettings settings, BlockCondition blockCondition, Component message, boolean setSpawnPoint, int priority) {
        super(settings);
        this.blockCondition = blockCondition;
        this.message = message;
        this.setSpawnPoint = setSpawnPoint;
        this.priority = priority;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public Component getMessage() {
        return this.message;
    }

    public boolean shouldSetSpawnPoint() {
        return this.setSpawnPoint;
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
    public static void preventSleep(CanPlayerSleepEvent event) {
        OriginDataHolder.get(event.getEntity()).streamActivePowers(PreventSleepPower.class).forEach(x -> {
            if (x.blockCondition.test(event.getLevel(), event.getPos())) {
                if (x.shouldSetSpawnPoint())
                    event.getEntity().setRespawnPosition(event.getLevel().dimension(), event.getPos(), 0.0F, false, true);
                event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
                event.getEntity().displayClientMessage(x.message, true);
            }
        });
    }
}
