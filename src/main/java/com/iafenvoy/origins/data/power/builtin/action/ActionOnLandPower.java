package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnLandPower extends Power {
    public static final MapCodec<ActionOnLandPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action").forGetter(ActionOnLandPower::getEntityAction)
    ).apply(i, ActionOnLandPower::new));
    private final EntityAction entityAction;

    public ActionOnLandPower(BaseSettings settings, EntityAction entityAction) {
        super(settings);
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
    public static void onFall(LivingFallEvent event) {
        LivingEntity living = event.getEntity();
        OriginDataHolder.get(living).streamActivePowers(ActionOnLandPower.class).forEach(x -> x.getEntityAction().execute(living));
    }
}
