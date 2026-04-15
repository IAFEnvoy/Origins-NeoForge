package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.component.PowerComponent;
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

import java.util.List;

@EventBusSubscriber
public record ClimbingPower(boolean allowHolding, EntityCondition holdCondition) implements Power, Toggleable {
    public static final MapCodec<ClimbingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("allow_holding", true).forGetter(ClimbingPower::allowHolding),
            EntityCondition.optionalCodec("hold_condition").forGetter(ClimbingPower::holdCondition)
    ).apply(i, ClimbingPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public List<PowerComponent> createComponents() {
        return List.of(new ToggleComponent());
    }

    @Override
    public void toggle(OriginDataHolder holder, int index) {
        holder.getComponentFor(this, ToggleComponent.class).ifPresent(ToggleComponent::toggle);
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
        OriginDataHolder holder = OriginDataHolder.get(player);
        if (holder.getComponentFor(this, ToggleComponent.class).map(ToggleComponent::isActive).orElse(false)) {
//            climbingPosSetter.accept(player.blockPosition());
            return true;
        }
        return player.isSuppressingSlidingDownLadder() && this.allowHolding && this.holdCondition.test(player);
    }
}
