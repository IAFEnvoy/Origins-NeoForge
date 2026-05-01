package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ActionOnCallbackPower extends Power {
    public static final MapCodec<ActionOnCallbackPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityAction.optionalCodec("entity_action_respawned").forGetter(ActionOnCallbackPower::getEntityActionRespawned),
            EntityAction.optionalCodec("entity_action_removed").forGetter(ActionOnCallbackPower::getEntityActionRemoved),
            EntityAction.optionalCodec("entity_action_gained").forGetter(ActionOnCallbackPower::getEntityActionGained),
            EntityAction.optionalCodec("entity_action_lost").forGetter(ActionOnCallbackPower::getEntityActionLost),
            EntityAction.optionalCodec("entity_action_added").forGetter(ActionOnCallbackPower::getEntityActionAdded)
    ).apply(i, ActionOnCallbackPower::new));
    private final EntityAction entityActionRespawned;
    private final EntityAction entityActionRemoved;
    private final EntityAction entityActionGained;
    private final EntityAction entityActionLost;
    private final EntityAction entityActionAdded;

    public ActionOnCallbackPower(BaseSettings settings, EntityAction entityActionRespawned, EntityAction entityActionRemoved, EntityAction entityActionGained, EntityAction entityActionLost, EntityAction entityActionAdded) {
        super(settings);
        this.entityActionRespawned = entityActionRespawned;
        this.entityActionRemoved = entityActionRemoved;
        this.entityActionGained = entityActionGained;
        this.entityActionLost = entityActionLost;
        this.entityActionAdded = entityActionAdded;
    }

    public EntityAction getEntityActionRespawned() {
        return this.entityActionRespawned;
    }

    public EntityAction getEntityActionRemoved() {
        return this.entityActionRemoved;
    }

    public EntityAction getEntityActionGained() {
        return this.entityActionGained;
    }

    public EntityAction getEntityActionLost() {
        return this.entityActionLost;
    }

    public EntityAction getEntityActionAdded() {
        return this.entityActionAdded;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull OriginDataHolder holder) {
        this.entityActionGained.execute(holder.getEntity());
    }

    @Override
    public void revoke(@NotNull OriginDataHolder holder) {
        this.entityActionLost.execute(holder.getEntity());
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        this.entityActionAdded.execute(holder.getEntity());
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        this.entityActionRemoved.execute(holder.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!event.isEndConquered())
            OriginDataHolder.get(player).streamActivePowers(ActionOnCallbackPower.class).map(ActionOnCallbackPower::getEntityActionRespawned).forEach(x -> x.execute(player));
    }
}
