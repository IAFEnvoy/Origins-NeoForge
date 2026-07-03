package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record HudRender(boolean shouldRenderInActive, ResourceLocation spriteLocation, int barIndex, int iconIndex,
                        EntityCondition condition, boolean inverted, int order) {
    public static final ResourceLocation DEFAULT_SPRITE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "textures/gui/resource_bar.png");
    public static final Codec<HudRender> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("should_render_inactive", true).forGetter(HudRender::shouldRenderInActive),
            ResourceLocation.CODEC.optionalFieldOf("sprite_location", DEFAULT_SPRITE).forGetter(HudRender::spriteLocation),
            Codec.INT.optionalFieldOf("bar_index", 0).forGetter(HudRender::barIndex),
            Codec.INT.optionalFieldOf("icon_index", 0).forGetter(HudRender::iconIndex),
            EntityCondition.optionalCodec("condition").forGetter(HudRender::condition),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(HudRender::inverted),
            Codec.INT.optionalFieldOf("order", 0).forGetter(HudRender::order)
    ).apply(i, HudRender::new));
}
