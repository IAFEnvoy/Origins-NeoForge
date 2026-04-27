package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.client.NightVisionStrengthEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(Dist.CLIENT)
public class NightVisionPower extends Power {
    public static final MapCodec<NightVisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.floatRange(0, 1).optionalFieldOf("strength", 1F).forGetter(NightVisionPower::getStrength),
            EntityCondition.optionalCodec("condition").forGetter(NightVisionPower::getCondition)
    ).apply(i, NightVisionPower::new));
    private final float strength;
    private final EntityCondition condition;

    public NightVisionPower(BaseSettings settings, float strength, EntityCondition condition) {
        super(settings);
        this.strength = strength;
        this.condition = condition;
    }

    public float getStrength() {
        return this.strength;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void handleNightVisionStrength(NightVisionStrengthEvent event) {
        Entity entity = event.getEntity();
        for (NightVisionPower power : OriginDataHolder.get(entity).getPowers(RegularPowers.NIGHT_VISION, NightVisionPower.class))
            if (power.condition.test(entity)) {
                event.setStrength(power.strength);
                break;
            }
    }
}
