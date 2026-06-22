package com.iafenvoy.origins.network.payload;

import com.iafenvoy.origins.Origins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RecipeBadgeS2CPayload(Identifier recipe, int width, List<ItemStack> inputs, ItemStack result)
        implements CustomPacketPayload {
    public static final Type<RecipeBadgeS2CPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Origins.MOD_ID, "recipe_badge_s2c")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeBadgeS2CPayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, RecipeBadgeS2CPayload::recipe,
            ByteBufCodecs.VAR_INT, RecipeBadgeS2CPayload::width,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, RecipeBadgeS2CPayload::inputs,
            ItemStack.OPTIONAL_STREAM_CODEC, RecipeBadgeS2CPayload::result,
            RecipeBadgeS2CPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
