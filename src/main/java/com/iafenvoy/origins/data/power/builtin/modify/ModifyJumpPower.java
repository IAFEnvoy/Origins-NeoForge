package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class ModifyJumpPower extends Power {
    public static final MapCodec<ModifyJumpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyJumpPower::getModifier),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyJumpPower::getEntityAction)
    ).apply(i, ModifyJumpPower::new));
    private final List<Modifier> modifier;
    private final EntityAction entityAction;

    public ModifyJumpPower(BaseSettings settings, List<Modifier> modifier, EntityAction entityAction) {
        super(settings);
        this.modifier = modifier;
        this.entityAction = entityAction;
    }

    public List<Modifier> getModifier() {
        return this.modifier;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    /**
     * This needs to be executed after COMBAT's jump overhaul.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        Entity player = event.getEntity();
        double modified = OriginDataHolder.get(player).streamActivePowers(ModifyJumpPower.class).reduce(event.getEntity().getDeltaMovement().y, (value, power) -> {
            power.entityAction.execute(player);
            return Modifier.applyModifiers(power.modifier, value);
        }, Double::sum);
        Vec3 vel = player.getDeltaMovement();
        double delta = modified - vel.y;
        if (delta == 0) return;
        player.setDeltaMovement(vel.add(0, delta, 0));
    }
}
