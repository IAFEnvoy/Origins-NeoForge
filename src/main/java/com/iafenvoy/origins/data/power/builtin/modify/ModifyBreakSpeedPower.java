package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyBreakSpeedPower extends Power {
    public static final MapCodec<ModifyBreakSpeedPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyBreakSpeedPower::getModifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyBreakSpeedPower::getBlockCondition),
            EntityCondition.optionalCodec("condition").forGetter(ModifyBreakSpeedPower::getCondition)
    ).apply(i, ModifyBreakSpeedPower::new));
    private final List<Modifier> modifiers;
    private final BlockCondition blockCondition;
    private final EntityCondition condition;

    public ModifyBreakSpeedPower(BaseSettings settings, List<Modifier> modifiers, BlockCondition blockCondition, EntityCondition condition) {
        super(settings);
        this.modifiers = modifiers;
        this.blockCondition = blockCondition;
        this.condition = condition;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
