package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyHarvestPower extends Power {
    public static final MapCodec<ModifyHarvestPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyHarvestPower::getBlockCondition),
            Codec.BOOL.fieldOf("allow").forGetter(ModifyHarvestPower::isAllow)
    ).apply(i, ModifyHarvestPower::new));
    private final BlockCondition blockCondition;
    private final boolean allow;

    public ModifyHarvestPower(BaseSettings settings, BlockCondition blockCondition, boolean allow) {
        super(settings);
        this.blockCondition = blockCondition;
        this.allow = allow;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public boolean isAllow() {
        return this.allow;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void checkCanHarvest(PlayerEvent.HarvestCheck event) {
        if (event.getLevel() instanceof Level level)
            OriginDataHolder.get(event.getEntity()).streamActivePowers(ModifyHarvestPower.class)
                    .filter(x -> x.getBlockCondition().test(level, event.getPos()))
                    .map(ModifyHarvestPower::isAllow)
                    .reduce((x, y) -> x || y)
                    .ifPresent(event::setCanHarvest);
    }
}
