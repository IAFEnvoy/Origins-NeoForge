package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//如果 origin 为空 = 随机
public record ChooseOriginC2SPayload(Holder<Layer> layer,
                                     Optional<Holder<Origin>> origin,
                                     boolean firstJoin) implements CustomPacketPayload {
    public static final Type<ChooseOriginC2SPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseOriginC2SPayload> STREAM_CODEC = StreamCodec.composite(
            Layer.STREAM_CODEC, ChooseOriginC2SPayload::layer,
            ByteBufCodecs.optional(Origin.STREAM_CODEC), ChooseOriginC2SPayload::origin,
            ByteBufCodecs.BOOL, ChooseOriginC2SPayload::firstJoin,
            ChooseOriginC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}