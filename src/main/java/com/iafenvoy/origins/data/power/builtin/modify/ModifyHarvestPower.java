package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyHarvestPower extends Power {
    public static final MapCodec<ModifyHarvestPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyHarvestPower::getModifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyHarvestPower::getBlockCondition),
            Codec.BOOL.fieldOf("allow").forGetter(ModifyHarvestPower::isAllow)
    ).apply(i, ModifyHarvestPower::new));
    private final List<Modifier> modifiers;
    private final BlockCondition blockCondition;
    private final boolean allow;

    public ModifyHarvestPower(BaseSettings settings, List<Modifier> modifiers, BlockCondition blockCondition, boolean allow) {
        super(settings);
        this.modifiers = modifiers;
        this.blockCondition = blockCondition;
        this.allow = allow;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public boolean isAllow() {
        return this.allow;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public double apply(double baseValue) {
        return Modifier.applyModifiers(this.modifiers, baseValue);
    }
}
