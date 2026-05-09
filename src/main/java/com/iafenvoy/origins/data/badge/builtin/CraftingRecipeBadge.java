package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record CraftingRecipeBadge(ResourceLocation sprite, ResourceLocation recipe, boolean fromPower,
                                  Optional<Component> prefix, Optional<Component> suffix) implements Badge {
    public static final MapCodec<CraftingRecipeBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(CraftingRecipeBadge::sprite),
            ResourceLocation.CODEC.fieldOf("recipe").forGetter(CraftingRecipeBadge::recipe),
            Codec.BOOL.optionalFieldOf("from_power", false).forGetter(CraftingRecipeBadge::fromPower),
            MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("prefix").forGetter(CraftingRecipeBadge::prefix),
            MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("suffix").forGetter(CraftingRecipeBadge::suffix)
    ).apply(i, CraftingRecipeBadge::new));

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }
}
