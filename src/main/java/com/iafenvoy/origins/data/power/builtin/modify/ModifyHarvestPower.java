package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ModifyHarvestPower(BlockCondition blockCondition,boolean allow) implements Power {

    public static final MapCodec<ModifyHarvestPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyHarvestPower::blockCondition),
            Codec.BOOL.fieldOf("allow").forGetter(ModifyHarvestPower::allow)
    ).apply(i, ModifyHarvestPower::new));

// TODO ListConfiguration

//public static final Codec<ModifyHarvestConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//        ListConfiguration.MODIFIER_CODEC.forGetter(ModifyHarvestConfiguration::modifiers),
//        ConfiguredBlockCondition.optional("block_condition").forGetter(ModifyHarvestConfiguration::condition),
//        CalioCodecHelper.BOOL.fieldOf("allow").forGetter(ModifyHarvestConfiguration::allow)
//).apply(instance, ModifyHarvestConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    // TODO
//
//    public static Optional<Boolean> isHarvestAllowed(Player player, LevelReader reader, BlockPos pos) {
//        return PowerContainer.getPowers(player, ApoliPowers.MODIFY_HARVEST.get()).stream()
//                .filter(x -> x.value().getFactory().doesApply(x.value(), reader, pos))
//                .map(x -> x.value().getFactory().isHarvestAllowed(x.value()))
//                .reduce((x, y) -> x || y);
//    }


    public boolean doesApply(Level level, BlockPos pos) {
        return blockCondition().test(level, pos);
    }

    public boolean isHarvestAllowed() {
        return allow();
    }
}
