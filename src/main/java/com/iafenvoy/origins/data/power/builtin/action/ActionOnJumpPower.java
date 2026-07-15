package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnJumpPower extends HasCooldownPower {
    public static final MapCodec<ActionOnJumpPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(ActionOnJumpPower::getCooldown),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnJumpPower::getEntityAction)
    ).apply(instance, ActionOnJumpPower::new));
    private final EntityAction entityAction;

    public ActionOnJumpPower(BaseSettings settings, CooldownSettings cooldown, EntityAction entityAction) {
        super(settings, cooldown);
        this.entityAction = entityAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        PowerHelper.get(entity).execute(ActionOnJumpPower.class,
                (holder, power) -> power.getCooldownComponent(holder).useIfReady(() -> power.entityAction.execute(entity)));
    }
}
