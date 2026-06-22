package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyBreakSpeedPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyBreakSpeedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.optionalFieldOf("modifier", List.of()).forGetter(ModifyBreakSpeedPower::getModifier),
            CombinedCodecs.MODIFIER.optionalFieldOf("hardness_modifier", List.of()).forGetter(ModifyBreakSpeedPower::getHardnessModifier),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBreakSpeedPower::getBlockCondition)
    ).apply(i, ModifyBreakSpeedPower::new));
    private final List<Modifier> modifier, hardnessModifier;
    private final BlockCondition blockCondition;

    public ModifyBreakSpeedPower(BaseSettings settings, List<Modifier> modifier, List<Modifier> hardnessModifier, BlockCondition blockCondition) {
        super(settings);
        this.modifier = modifier;
        this.hardnessModifier = hardnessModifier;
        this.blockCondition = blockCondition;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public List<Modifier> getHardnessModifier() {
        return this.hardnessModifier;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
