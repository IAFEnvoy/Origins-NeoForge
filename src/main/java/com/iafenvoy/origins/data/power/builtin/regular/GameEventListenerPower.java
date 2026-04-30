package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@NotImplementedYet
public class GameEventListenerPower extends HasCooldownPower {
    public static final MapCodec<GameEventListenerPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(HasCooldownPower::getCooldown),
            ExtraEnumCodecs.GAME_EVENT_DELIVERY_MODE.optionalFieldOf("delivery_mode", GameEventListener.DeliveryMode.UNSPECIFIED).forGetter(GameEventListenerPower::getTriggerOrder),
            Codec.BOOL.optionalFieldOf("entity", true).forGetter(GameEventListenerPower::includeEntity),
            Codec.BOOL.optionalFieldOf("block", true).forGetter(GameEventListenerPower::includeBlock),
            BiEntityAction.optionalCodec("bi_entity_action").forGetter(GameEventListenerPower::getBiEntityAction),
            BiEntityCondition.optionalCodec("bi_entity_condition").forGetter(GameEventListenerPower::getBiEntityCondition),
            BlockAction.optionalCodec("block_action").forGetter(GameEventListenerPower::getBlockAction),
            BlockCondition.optionalCodec("block_condition").forGetter(GameEventListenerPower::getBlockCondition),
            CombinedCodecs.GAME_EVENT.optionalFieldOf("event", List.of()).forGetter(GameEventListenerPower::getEvent),
            TagKey.codec(Registries.GAME_EVENT).optionalFieldOf("event_tag").forGetter(GameEventListenerPower::getEventTag),
            Codec.BOOL.optionalFieldOf("show_particle", true).forGetter(GameEventListenerPower::shouldShowParticle)
    ).apply(i, GameEventListenerPower::new));
    private final GameEventListener.DeliveryMode triggerOrder;
    private final boolean entity, block;
    private final BiEntityAction biEntityAction;
    private final BiEntityCondition biEntityCondition;
    private final BlockAction blockAction;
    private final BlockCondition blockCondition;
    private final List<Holder<GameEvent>> event;
    private final Optional<TagKey<GameEvent>> eventTag;
    private final boolean showParticle;

    public GameEventListenerPower(BaseSettings settings, CooldownSettings cooldown, GameEventListener.DeliveryMode triggerOrder, boolean entity, boolean block, BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, BlockAction blockAction, BlockCondition blockCondition, List<Holder<GameEvent>> event, Optional<TagKey<GameEvent>> eventTag, boolean showParticle) {
        super(settings, cooldown);
        this.triggerOrder = triggerOrder;
        this.entity = entity;
        this.block = block;
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.blockAction = blockAction;
        this.blockCondition = blockCondition;
        this.event = event;
        this.eventTag = eventTag;
        this.showParticle = showParticle;
    }

    public GameEventListener.DeliveryMode getTriggerOrder() {
        return this.triggerOrder;
    }

    public boolean includeEntity() {
        return this.entity;
    }

    public boolean includeBlock() {
        return this.block;
    }

    public BiEntityAction getBiEntityAction() {
        return this.biEntityAction;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public List<Holder<GameEvent>> getEvent() {
        return this.event;
    }

    public Optional<TagKey<GameEvent>> getEventTag() {
        return this.eventTag;
    }

    public boolean shouldShowParticle() {
        return this.showParticle;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
