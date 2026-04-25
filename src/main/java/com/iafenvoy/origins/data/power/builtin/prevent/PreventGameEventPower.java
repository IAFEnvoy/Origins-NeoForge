package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.VanillaGameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber
public class PreventGameEventPower extends Power {
    public static final MapCodec<PreventGameEventPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.either(CombinedCodecs.GAME_EVENT, TagKey.codec(Registries.GAME_EVENT)).optionalFieldOf("event", Either.left(List.of())).forGetter(PreventGameEventPower::getEvent),
            EntityAction.optionalCodec("entity_action").forGetter(PreventGameEventPower::getEntityAction)
    ).apply(i, PreventGameEventPower::new));
    private final Either<List<Holder<GameEvent>>, TagKey<GameEvent>> event;
    private final EntityAction entityAction;

    public PreventGameEventPower(BaseSettings settings, Either<List<Holder<GameEvent>>, TagKey<GameEvent>> event, EntityAction entityAction) {
        super(settings);
        this.event = event;
        this.entityAction = entityAction;
    }

    public Either<List<Holder<GameEvent>>, TagKey<GameEvent>> getEvent() {
        return this.event;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventGameEvent(VanillaGameEvent event) {
        Entity entity = event.getCause();
        if (entity == null) return;
        List<PreventGameEventPower> list = OriginDataHolder.get(entity)
                .streamActivePowers(PreventGameEventPower.class)
                .filter(x -> x.getEvent().map(l -> l.stream().anyMatch(e -> e.value() == event.getVanillaEvent().value()), tag -> event.getVanillaEvent().is(tag)))
                .toList();
        if (!list.isEmpty()) {
            list.forEach(x -> x.getEntityAction().execute(entity));
            event.setCanceled(true);
        }
    }
}
