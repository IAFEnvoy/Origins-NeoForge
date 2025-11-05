package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public record ModifyFallingPower(double velocity,boolean takeFallDamage) implements Power {

    public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.DOUBLE.optionalFieldOf("velocity", 0.0).forGetter(ModifyFallingPower::velocity),
        Codec.BOOL.optionalFieldOf("take_fall_damage", true).forGetter(ModifyFallingPower::takeFallDamage)
    ).apply(i, ModifyFallingPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyFallingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ExtraCodecs.strictOptionalField(CalioCodecHelper.DOUBLE, "velocity").forGetter(ModifyFallingConfiguration::velocity),
//            ExtraCodecs.strictOptionalField(CalioCodecHelper.BOOL, "take_fall_damage", true).forGetter(ModifyFallingConfiguration::takeFallDamage),
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyFallingConfiguration::modifiers)
//    ).apply(instance, ModifyFallingConfiguration::new));
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static double apply(Entity entity, double originalValue) {
        if (!(entity instanceof LivingEntity living))
            return originalValue;
        AttributeInstance attribute = living.getAttribute(Attributes.GRAVITY);
        if (attribute != null) {
            // TODO
//            double modifier = PowerContainer.modify(entity, ApoliPowers.MODIFY_FALLING.get(), originalValue);
//            if (modifier != originalValue && modifier >= 0.0) {
//                return modifier;
//            }
        }
        return originalValue;
    }
}
