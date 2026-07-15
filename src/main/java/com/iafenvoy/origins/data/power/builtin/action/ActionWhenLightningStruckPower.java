package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionWhenLightningStruckPower extends HasCooldownPower {
    public static final MapCodec<ActionWhenLightningStruckPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(ActionWhenLightningStruckPower::getCooldown),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionWhenLightningStruckPower::getBiEntityCondition),
            EntityAction.optionalCodec("entity_action").forGetter(ActionWhenLightningStruckPower::getEntityAction)
    ).apply(instance, ActionWhenLightningStruckPower::new));
    private final BiEntityCondition bientityCondition;
    private final EntityAction entityAction;

    public ActionWhenLightningStruckPower(BaseSettings settings, CooldownSettings cooldown,
                                           BiEntityCondition bientityCondition, EntityAction entityAction) {
        super(settings, cooldown);
        this.bientityCondition = bientityCondition;
        this.entityAction = entityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.bientityCondition;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onStruck(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        PowerHelper.get(entity).execute(ActionWhenLightningStruckPower.class,
                power -> power.bientityCondition.test(entity, event.getLightning()),
                (holder, power) -> power.getCooldownComponent(holder).useIfReady(() -> power.entityAction.execute(entity)));
    }
}
