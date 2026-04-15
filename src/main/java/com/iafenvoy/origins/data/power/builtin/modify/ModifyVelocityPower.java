package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModifyVelocityPower extends Power {
    public static final MapCodec<ModifyVelocityPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyVelocityPower::getModifiers),
            ExtraEnumCodecs.AXIS.listOf().fieldOf("axis").forGetter(e -> new ArrayList<>(e.getAxes()))
    ).apply(i, (s, m, e) -> new ModifyVelocityPower(s, m, Set.copyOf(e))));
    private final List<Modifier> modifiers;
    private final Set<Direction.AxisDirection> axes;

    public ModifyVelocityPower(BaseSettings settings, List<Modifier> modifiers, Set<Direction.AxisDirection> axes) {
        super(settings);
        this.modifiers = modifiers;
        this.axes = axes;
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public Set<Direction.AxisDirection> getAxes() {
        return this.axes;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
