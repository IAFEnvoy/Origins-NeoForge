package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//if origin is empty = random
public record ChooseOriginC2SPayload(Holder<Layer> layer,
                                     Optional<Holder<Origin>> origin) implements CustomPacketPayload {
    public static final Type<ChooseOriginC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseOriginC2SPayload> STREAM_CODEC = StreamCodec.composite(
            Layer.STREAM_CODEC, ChooseOriginC2SPayload::layer,
            ByteBufCodecs.optional(Origin.STREAM_CODEC), ChooseOriginC2SPayload::origin,
            ChooseOriginC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
