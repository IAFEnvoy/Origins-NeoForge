package com.iafenvoy.origins.data.power.builtin.action;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.PresetBadges;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/** Executes an action after holding an active key, with optional repeated actions while it remains held. */
public final class HoldActionPower extends Power implements Toggleable {
    public static final MapCodec<HoldActionPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            KeySettings.CODEC.forGetter(HoldActionPower::configuredKey),
            Codec.INT.optionalFieldOf("hold_duration", 20).forGetter(HoldActionPower::holdDuration),
            EntityAction.CODEC.fieldOf("charged_action").forGetter(HoldActionPower::chargedAction),
            EntityAction.optionalCodec("entity_action").forGetter(HoldActionPower::entityAction),
            Codec.INT.optionalFieldOf("interval", 1).forGetter(HoldActionPower::interval),
            Codec.INT.optionalFieldOf("max_actions", 0).forGetter(HoldActionPower::maxActions)
    ).apply(instance, HoldActionPower::new));
    private static final Map<OriginDataHolder, Map<HoldActionPower, HoldState>> HOLD_STATES = new WeakHashMap<>();
    private final KeySettings configuredKey;
    private final int holdDuration;
    private final EntityAction chargedAction;
    private final EntityAction entityAction;
    private final int interval;
    private final int maxActions;

    public HoldActionPower(BaseSettings settings, KeySettings key, int holdDuration, EntityAction chargedAction,
                           EntityAction entityAction, int interval, int maxActions) {
        super(settings);
        this.configuredKey = key;
        this.holdDuration = Math.max(0, holdDuration);
        this.chargedAction = chargedAction;
        this.entityAction = entityAction;
        this.interval = Math.max(1, interval);
        this.maxActions = Math.max(0, maxActions);
    }

    public KeySettings configuredKey() { return this.configuredKey; }
    public int holdDuration() { return this.holdDuration; }
    public EntityAction chargedAction() { return this.chargedAction; }
    public EntityAction entityAction() { return this.entityAction; }
    public int interval() { return this.interval; }
    public int maxActions() { return this.maxActions; }

    @Override
    public KeySettings getKey() {
        // Hold powers always require held-key packets, even if the JSON omitted continuous.
        return new KeySettings(this.configuredKey.key(), true);
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        if (!this.getKey().match(key) || !this.isActive(holder)) return;
        this.state(holder).lastInputTick = holder.getEntity().level().getGameTime();
    }

    @Override
    public void activeTick(OriginDataHolder holder) {
        HoldState state = this.state(holder);
        long now = holder.getEntity().level().getGameTime();
        if (state.lastInputTick < 0 || now - state.lastInputTick > 1) {
            state.reset();
            return;
        }
        if (state.startTick < 0) state.startTick = now;
        long heldTicks = now - state.startTick;
        Entity entity = holder.getEntity();
        if (!state.charged && heldTicks >= this.holdDuration) {
            state.charged = true;
            this.chargedAction.execute(entity);
            state.lastActionTick = now;
        }
        if (state.charged && heldTicks > this.holdDuration && now - state.lastActionTick >= this.interval
                && (this.maxActions == 0 || state.actions < this.maxActions)) {
            this.entityAction.execute(entity);
            state.actions++;
            state.lastActionTick = now;
        }
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        this.state(holder).reset();
    }

    @Override
    public void collectBadges(ImmutableSet.Builder<Badge> builder) {
        super.collectBadges(builder);
        builder.add(PresetBadges.ACTIVE);
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() { return CODEC; }

    private HoldState state(OriginDataHolder holder) {
        return HOLD_STATES.computeIfAbsent(holder, ignored -> new IdentityHashMap<>())
                .computeIfAbsent(this, ignored -> new HoldState());
    }

    private static final class HoldState {
        private long startTick = -1;
        private long lastInputTick = -1;
        private long lastActionTick = -1;
        private int actions;
        private boolean charged;
        private void reset() {
            this.startTick = -1;
            this.lastInputTick = -1;
            this.lastActionTick = -1;
            this.actions = 0;
            this.charged = false; }
    }
}
