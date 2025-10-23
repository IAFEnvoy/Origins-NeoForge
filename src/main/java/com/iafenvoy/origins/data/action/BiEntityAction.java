package com.iafenvoy.origins.data.action;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

public interface BiEntityAction extends BiConsumer<Entity, Entity> {
    Codec<BiEntityAction> CODEC = ActionRegistries.BI_ENTITY_ACTION.byNameCodec().dispatch("type", BiEntityAction::type, ActionType::codec);

    ActionType<BiEntityAction> type();

    @Override
    void accept(Entity source, Entity target);
}
