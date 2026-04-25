package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public enum ReloadLevelRendererS2CPayload implements CustomPacketPayload {
    INSTANCE;
    public static final Type<ReloadLevelRendererS2CPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "reload_level_renderer_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadLevelRendererS2CPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
