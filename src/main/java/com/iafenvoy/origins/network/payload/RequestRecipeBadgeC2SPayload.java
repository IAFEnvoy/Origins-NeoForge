package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record RequestRecipeBadgeC2SPayload(Identifier recipe) implements CustomPacketPayload {
    public static final Type<RequestRecipeBadgeC2SPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Origins.MOD_ID, "request_recipe_badge_c2s")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestRecipeBadgeC2SPayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, RequestRecipeBadgeC2SPayload::recipe,
            RequestRecipeBadgeC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
