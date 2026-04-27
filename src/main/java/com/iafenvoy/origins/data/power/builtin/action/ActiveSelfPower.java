package com.iafenvoy.origins.data.power.builtin.action;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class ActiveSelfPower extends HasCooldownPower implements Toggleable {
    public static final MapCodec<ActiveSelfPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            KeySettings.CODEC.forGetter(ActiveSelfPower::getKey),
            EntityAction.CODEC.fieldOf("entity_action").forGetter(ActiveSelfPower::getEntityAction)
    ).apply(i, ActiveSelfPower::new));
    private final KeySettings key;
    private final EntityAction entityAction;

    public ActiveSelfPower(BaseSettings settings, CooldownSettings cooldown, KeySettings key, EntityAction entityAction) {
        super(settings, cooldown);
        this.key = key;
        this.entityAction = entityAction;
    }

    public KeySettings getKey() {
        return this.key;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        this.getCooldownComponent(holder).useIfReady(() -> {
            if (this.key.match(key)) this.entityAction.execute(holder.entity());
        });
    }
}
