package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntitySetAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class OriginsAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Origins.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySetAttachment>> ENTITY_SET = REGISTRY.register("entity_set", () -> AttachmentType.builder(EntitySetAttachment::new).serialize(EntitySetAttachment.CODEC).copyOnDeath().build());
}
