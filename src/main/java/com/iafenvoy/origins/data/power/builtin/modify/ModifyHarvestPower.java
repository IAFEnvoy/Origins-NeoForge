package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyHarvestPower(List<Modifier> modifiers, BlockCondition blockCondition, boolean allow) implements Power {

    public static final MapCodec<ModifyHarvestPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyHarvestPower::modifiers),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyHarvestPower::blockCondition),
            Codec.BOOL.fieldOf("allow").forGetter(ModifyHarvestPower::allow)
    ).apply(i, ModifyHarvestPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean doesApply(Level level, BlockPos pos) {
        return blockCondition().test(level, pos);
    }

    public boolean isHarvestAllowed() {
        return allow();
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(modifiers, baseValue);
    }
}
