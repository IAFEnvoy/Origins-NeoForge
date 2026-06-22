package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class OriginsAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Origins.MOD_ID);

    //不要直接使用这些来调用！！！
    //请使用 OriginDataHolder.get() 代替
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<EntityOriginAttachment>> ENTITY_ORIGIN = REGISTRY.register("entity_origin", () -> AttachmentType.builder(EntityOriginAttachment::new).serialize(EntityOriginAttachment.MAP_CODEC).sync(EntityOriginAttachment.STREAM_CODEC).copyOnDeath().build());
}
