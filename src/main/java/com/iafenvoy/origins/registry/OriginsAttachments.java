package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber
public final class OriginsAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Origins.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityOriginAttachment>> ENTITY_ORIGIN = REGISTRY.register("entity_origin", () -> AttachmentType.builder(EntityOriginAttachment::new).serialize(EntityOriginAttachment.CODEC).copyOnDeath().build());

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        entity.getData(ENTITY_ORIGIN).tick(entity);
    }
}
