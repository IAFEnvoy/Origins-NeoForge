package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/** Prevents matching mobs from selecting the holder as their attack target. */
public final class PreventTargetingPower extends Power {
    public static final MapCodec<PreventTargetingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("mob_condition").forGetter(PreventTargetingPower::mobCondition)
    ).apply(instance, PreventTargetingPower::new));
    private final EntityCondition mobCondition;

    public PreventTargetingPower(BaseSettings settings, EntityCondition mobCondition) {
        super(settings);
        this.mobCondition = mobCondition;
    }

    public EntityCondition mobCondition() {
        return this.mobCondition;
    }

    public static boolean prevents(Entity target, Entity mob) {
        return PowerHelper.get(target).anyActive(PreventTargetingPower.class, power -> power.mobCondition.test(mob));
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
