package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record MountPlayerS2CPayload(int source, int target) implements CustomPacketPayload {
    public static final Type<MountPlayerS2CPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "mount_player_s2c"));
    public static final StreamCodec<ByteBuf, MountPlayerS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MountPlayerS2CPayload::source,
            ByteBufCodecs.INT, MountPlayerS2CPayload::target,
            MountPlayerS2CPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
