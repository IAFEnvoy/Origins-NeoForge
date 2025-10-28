package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ConfirmOriginS2CPayload(Holder<Layer> layer, Holder<Origin> origin) implements CustomPacketPayload {
    public static final Type<ConfirmOriginS2CPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "confirm_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfirmOriginS2CPayload> STREAM_CODEC = StreamCodec.composite(
            Layer.STREAM_CODEC, ConfirmOriginS2CPayload::layer,
            Origin.STREAM_CODEC, ConfirmOriginS2CPayload::origin,
            ConfirmOriginS2CPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
