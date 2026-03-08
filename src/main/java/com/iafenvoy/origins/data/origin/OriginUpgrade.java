package com.iafenvoy.origins.data.origin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Legacy/standalone origin upgrade record.
 * See also {@link Origin.Upgrade} for the inline version used by {@link Origin}.
 */
public record OriginUpgrade(ResourceLocation advancementCondition, ResourceLocation upgradeToOrigin,
                             @Nullable String announcement) {
    public static final Codec<OriginUpgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("condition").forGetter(OriginUpgrade::advancementCondition),
            ResourceLocation.CODEC.fieldOf("origin").forGetter(OriginUpgrade::upgradeToOrigin),
            Codec.STRING.optionalFieldOf("announcement").forGetter(u -> Optional.ofNullable(u.announcement()))
    ).apply(i, (condition, origin, announcement) -> new OriginUpgrade(condition, origin, announcement.orElse(null))));
}
