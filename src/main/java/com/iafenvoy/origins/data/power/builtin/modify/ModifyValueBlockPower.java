package com.iafenvoy.origins.data.power.builtin.modify;

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
public class ModifyValueBlockPower extends Power {
    public static final MapCodec<ModifyValueBlockPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyValueBlockPower::getModifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyValueBlockPower::getCondition)
    ).apply(i, ModifyValueBlockPower::new));
    private final List<Modifier> modifiers;
    private final BlockCondition condition;

    public ModifyValueBlockPower(BaseSettings settings, List<Modifier> modifiers, BlockCondition condition) {
        super(settings);
        this.modifiers = modifiers;
        this.condition = condition;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
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

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
