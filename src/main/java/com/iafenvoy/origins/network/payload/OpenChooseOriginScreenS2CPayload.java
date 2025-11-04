package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenChooseOriginScreenS2CPayload(boolean showBackground) implements CustomPacketPayload {
    public static final Type<OpenChooseOriginScreenS2CPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "open_choose_origin_screen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenChooseOriginScreenS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OpenChooseOriginScreenS2CPayload::showBackground,
            OpenChooseOriginScreenS2CPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
