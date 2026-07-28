package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public enum OpenEditorS2CPayload implements CustomPacketPayload {
    INSTANCE;
    public static final CustomPacketPayload.Type<OpenEditorS2CPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "open_editor_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEditorS2CPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
