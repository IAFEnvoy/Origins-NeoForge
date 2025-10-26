package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
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
public record NightVisionPower(float strength, EntityCondition condition) implements Power {
    public static final MapCodec<NightVisionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(0, 1).optionalFieldOf("strength", 1F).forGetter(NightVisionPower::strength),
            EntityCondition.optionalCodec("condition").forGetter(NightVisionPower::condition)
    ).apply(i, NightVisionPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void handleNightVisionStrength(NightVisionStrengthEvent event) {
        Entity entity = event.getEntity();
        for (NightVisionPower power : EntityOriginAttachment.get(entity).getPowers(RegularPowers.NIGHT_VISION, NightVisionPower.class))
            if (power.condition.test(entity)) {
                event.setStrength(power.strength);
                break;
            }
    }
}
