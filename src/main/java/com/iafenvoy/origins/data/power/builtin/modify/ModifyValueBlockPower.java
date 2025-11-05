package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyValueBlockPower(BlockCondition condition) implements Power {

    public static final MapCodec<ModifyValueBlockPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyValueBlockPower::condition)
    ).apply(i, ModifyValueBlockPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyValueBlockConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyValueBlockConfiguration::modifiers),
//            ConfiguredBlockCondition.optional("block_condition").forGetter(ModifyValueBlockConfiguration::condition)
//    ).apply(instance, ModifyValueBlockConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
