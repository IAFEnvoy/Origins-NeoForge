package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.builtin.ToggleComponent;
import com.iafenvoy.origins.event.common.CanClimbEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ClimbingPower extends Power {
    public static final MapCodec<ClimbingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("allow_holding", true).forGetter(ClimbingPower::isAllowHolding),
            EntityCondition.optionalCodec("hold_condition").forGetter(ClimbingPower::getHoldCondition)
    ).apply(i, ClimbingPower::new));
    private final boolean allowHolding;
    private final EntityCondition holdCondition;

    public ClimbingPower(BaseSettings settings, boolean allowHolding, EntityCondition holdCondition) {
        super(settings);
        this.allowHolding = allowHolding;
        this.holdCondition = holdCondition;
    }

    public boolean isAllowHolding() {
        return this.allowHolding;
    }

    public EntityCondition getHoldCondition() {
        return this.holdCondition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean canHold(Entity entity) {
        return this.allowHolding && this.holdCondition.test(entity);
    }

    @SubscribeEvent
    public static void onClimbCheck(CanClimbEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            OriginDataHolder holder = OriginDataHolder.get(living);
            if (holder.streamPowers(ClimbingPower.class).anyMatch(power -> power.canClimb(living)))
                event.allow();
        }
    }

    public boolean canClimb(LivingEntity player) {
        return this.getSettings().condition().test(player);
    }
}
