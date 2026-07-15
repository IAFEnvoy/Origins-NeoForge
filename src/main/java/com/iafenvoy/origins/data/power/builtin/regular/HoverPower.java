package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class HoverPower extends Power {
    public static final MapCodec<HoverPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.FLOAT.optionalFieldOf("step_assist", 0F).forGetter(HoverPower::stepAssist)
    ).apply(instance, HoverPower::new));
    private final float stepAssist;

    public HoverPower(BaseSettings settings, float stepAssist) {
        super(settings);
        this.stepAssist = stepAssist;
    }

    public float stepAssist() {
        return this.stepAssist;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!PowerHelper.get(entity).anyActive(HoverPower.class)) return;
        Vec3 velocity = entity.getDeltaMovement();
        entity.setDeltaMovement(velocity.x, 0, velocity.z);
        entity.hurtMarked = true;
    }
}
