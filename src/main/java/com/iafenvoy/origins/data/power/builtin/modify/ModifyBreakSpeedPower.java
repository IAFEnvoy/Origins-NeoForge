package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyBreakSpeedPower extends Power {
    public static final MapCodec<ModifyBreakSpeedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyBreakSpeedPower::getModifier),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBreakSpeedPower::getBlockCondition)
    ).apply(i, ModifyBreakSpeedPower::new));
    private final List<Modifier> modifier;
    private final BlockCondition blockCondition;

    public ModifyBreakSpeedPower(BaseSettings settings, List<Modifier> modifier, BlockCondition blockCondition) {
        super(settings);
        this.modifier = modifier;
        this.blockCondition = blockCondition;
    }

    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
