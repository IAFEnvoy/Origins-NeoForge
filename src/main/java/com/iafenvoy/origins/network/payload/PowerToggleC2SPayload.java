package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PowerToggleC2SPayload(int index) implements CustomPacketPayload {
    public static final Type<PowerToggleC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "power_toggle_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PowerToggleC2SPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PowerToggleC2SPayload::index,
            PowerToggleC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
