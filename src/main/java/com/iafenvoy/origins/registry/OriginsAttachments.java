package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.attachment.EntitySetAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class OriginsAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Origins.MOD_ID);

    //Do not use these to call!!!
    //Use OriginDataHolder.get() etc. instead
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityOriginAttachment>> ENTITY_ORIGIN = REGISTRY.register("entity_origin", () -> AttachmentType.builder(EntityOriginAttachment::new).serialize(EntityOriginAttachment.CODEC).sync(EntityOriginAttachment.STREAM_CODEC).copyOnDeath().build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntitySetAttachment>> ENTITY_SET = REGISTRY.register("entity_set", () -> AttachmentType.builder(EntitySetAttachment::new).serialize(EntitySetAttachment.CODEC).sync(EntitySetAttachment.STREAM_CODEC).copyOnDeath().build());
}
