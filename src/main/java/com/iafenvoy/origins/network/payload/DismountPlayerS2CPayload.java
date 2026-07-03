package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DismountPlayerS2CPayload(int dismountingEntity) implements CustomPacketPayload {
    public static final Type<DismountPlayerS2CPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "dismount_player_s2c"));
    public static final StreamCodec<ByteBuf, DismountPlayerS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DismountPlayerS2CPayload::dismountingEntity,
            DismountPlayerS2CPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
