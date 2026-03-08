package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ChooseRandomOriginC2SPayload(Holder<Layer> layer) implements CustomPacketPayload {
    public static final Type<ChooseRandomOriginC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_random_origin_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseRandomOriginC2SPayload> STREAM_CODEC = StreamCodec.composite(
            Layer.STREAM_CODEC, ChooseRandomOriginC2SPayload::layer,
            ChooseRandomOriginC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
