package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.common.CanStandOnFluidEvent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public record StandOnFluidPower(TagKey<Fluid> fluid, EntityCondition condition) implements Power {
    public static final MapCodec<StandOnFluidPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            TagKey.codec(Registries.FLUID).fieldOf("fluid").forGetter(StandOnFluidPower::fluid),
            EntityCondition.optionalCodec("condition").forGetter(StandOnFluidPower::condition)
    ).apply(i, StandOnFluidPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void handleStandOnFluid(CanStandOnFluidEvent event) {
        LivingEntity living = event.getEntity();
        FluidState fluid = event.getFluid();
        for (StandOnFluidPower power : OriginDataHolder.get(living).getPowers(RegularPowers.STAND_ON_FLUID, StandOnFluidPower.class))
            if (fluid.is(power.fluid()) && power.condition().test(living)) {
                event.allow();
                return;
            }
    }
}
