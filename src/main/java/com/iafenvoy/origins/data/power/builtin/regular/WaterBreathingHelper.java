package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.mixin.accessor.LivingEntityAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;

public final class WaterBreathingHelper {
    private static final ResourceKey<DamageType> NO_WATER_FOR_GILLS = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "no_water_for_gills"));

    private WaterBreathingHelper() {}

    public static boolean shouldDrown(LivingEntity entity) {
        return !entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())
                && !entity.hasEffect(MobEffects.WATER_BREATHING)
                && !entity.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static void tick(LivingEntity entity) {
        List<WaterBreathingPower> powers = OriginDataHolder.get(entity).getPowers(RegularPowers.WATER_BREATHING, WaterBreathingPower.class);
        if (powers.isEmpty()) return;

        LivingEntityAccessor entityAccess = (LivingEntityAccessor) entity;
        if (shouldDrown(entity)) {
            int landGain = entityAccess.origins$callIncreaseAirSupply(0);
            int landLoss = entityAccess.origins$callDecreaseAirSupply(entity.getAirSupply());

            if (!entity.isInRain()) {
                entity.setAirSupply(landLoss - landGain);
                if (entity.getAirSupply() != -20) return;

                entity.setAirSupply(0);
                entity.hurt(new DamageSource(entity.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(NO_WATER_FOR_GILLS)), 2.0F);

                for (int i = 0; i < 8; ++i) {
                    double dx = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double dy = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double dz = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    entity.level().addParticle(ParticleTypes.BUBBLE,
                            entity.getRandomX(0.5), entity.getEyeY(), entity.getRandomZ(0.5),
                            dx * 0.5, dy * 0.5 + 0.25, dz * 0.5);
                }
            } else {
                entity.setAirSupply(entity.getAirSupply() - landGain);
            }
        } else if (entity.getAirSupply() < entity.getMaxAirSupply()) {
            entity.setAirSupply(entityAccess.origins$callIncreaseAirSupply(entity.getAirSupply()));
        }
    }
}
