package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PreventBlockUsePower extends Power {
    public static final MapCodec<PreventBlockUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(PreventBlockUsePower::getSettings),
            BlockCondition.CODEC.fieldOf("block_condition").forGetter(PreventBlockUsePower::getBlockCondition)
    ).apply(instance, PreventBlockUsePower::new));
    private final BlockCondition blockCondition;

    protected PreventBlockUsePower(BaseSettings settings, BlockCondition blockCondition) {
        super(settings);
        this.blockCondition = blockCondition;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        Entity entity = event.getEntity();
        if (OriginDataHolder.get(entity).streamActivePowers(PreventBlockUsePower.class).anyMatch(x -> x.blockCondition.test(entity.level(), event.getPos())))
            event.setUseBlock(TriState.FALSE);
    }
}
