package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyJumpPower(List<Modifier> modifiers, EntityAction entityAction) implements Power {

    public static final MapCodec<ModifyJumpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyJumpPower::modifiers),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyJumpPower::entityAction)
    ).apply(i, ModifyJumpPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public double apply(double baseValue) {
        return ModifierUtil.applyModifiers(this.modifiers, baseValue);
    }

    public void execute(Entity player) {
        this.entityAction().execute(player);
    }
}
