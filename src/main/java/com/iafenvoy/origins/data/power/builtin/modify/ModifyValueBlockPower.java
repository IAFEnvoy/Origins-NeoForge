package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyValueBlockPower(List<Modifier> modifiers, BlockCondition condition) implements Power {

    public static final MapCodec<ModifyValueBlockPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyValueBlockPower::modifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyValueBlockPower::condition)
    ).apply(i, ModifyValueBlockPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean test(Level level, BlockPos pos) {
        return this.condition.test(level, pos);
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(this.modifiers, baseValue);
    }
}
