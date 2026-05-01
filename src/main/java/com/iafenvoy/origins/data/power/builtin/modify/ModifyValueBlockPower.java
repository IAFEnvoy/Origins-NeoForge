package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyValueBlockPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyValueBlockPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyValueBlockPower::getModifier),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyValueBlockPower::getCondition)
    ).apply(i, ModifyValueBlockPower::new));
    private final List<Modifier> modifier;
    private final BlockCondition condition;

    public ModifyValueBlockPower(BaseSettings settings, List<Modifier> modifier, BlockCondition condition) {
        super(settings);
        this.modifier = modifier;
        this.condition = condition;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public BlockCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean test(Level level, BlockPos pos) {
        return this.condition.test(level, pos);
    }
}
