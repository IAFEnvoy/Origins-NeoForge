package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyValueBlockPower(List<Modifier> modifiers, BlockCondition condition) implements Power {

    public static final MapCodec<ModifyValueBlockPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyValueBlockPower::modifiers),
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
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
