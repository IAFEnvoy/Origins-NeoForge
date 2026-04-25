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
public class StandOnFluidPower extends Power {
    public static final MapCodec<StandOnFluidPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            TagKey.codec(Registries.FLUID).fieldOf("fluid").forGetter(StandOnFluidPower::getFluid),
            EntityCondition.optionalCodec("condition").forGetter(StandOnFluidPower::getCondition)
    ).apply(i, StandOnFluidPower::new));
    private final TagKey<Fluid> fluid;
    private final EntityCondition condition;

    public StandOnFluidPower(BaseSettings settings, TagKey<Fluid> fluid, EntityCondition condition) {
        super(settings);
        this.fluid = fluid;
        this.condition = condition;
    }

    public TagKey<Fluid> getFluid() {
        return this.fluid;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void handleStandOnFluid(CanStandOnFluidEvent event) {
        LivingEntity living = event.getEntity();
        FluidState fluid = event.getFluid();
        for (StandOnFluidPower power : OriginDataHolder.get(living).getPowers(RegularPowers.STAND_ON_FLUID, StandOnFluidPower.class))
            if (fluid.is(power.getFluid()) && power.getCondition().test(living)) {
                event.allow();
                return;
            }
    }
}
