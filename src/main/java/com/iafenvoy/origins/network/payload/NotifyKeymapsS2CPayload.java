package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public enum NotifyKeymapsS2CPayload implements CustomPacketPayload {
    INSTANCE;
    public static final Type<NotifyKeymapsS2CPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "notify_keymaps_s2c"));
    public static final StreamCodec<ByteBuf, NotifyKeymapsS2CPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
