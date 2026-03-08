package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.ListConfiguration;
import com.iafenvoy.origins.util.Modifier;
import com.iafenvoy.origins.util.ModifierUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModifyFallingPower(double velocity, boolean takeFallDamage, List<Modifier> modifiers) implements Power {

    public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("velocity", 0.0).forGetter(ModifyFallingPower::velocity),
            Codec.BOOL.optionalFieldOf("take_fall_damage", true).forGetter(ModifyFallingPower::takeFallDamage),
            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyFallingPower::modifiers)
    ).apply(i, ModifyFallingPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static double apply(Entity entity, double originalValue) {
        if (!(entity instanceof LivingEntity living))
            return originalValue;
        AttributeInstance attribute = living.getAttribute(Attributes.GRAVITY);
        if (attribute == null)
            return originalValue;
        return originalValue;
    }
}
