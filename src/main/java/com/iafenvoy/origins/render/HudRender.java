package com.iafenvoy.origins.render;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.codec.OptionalCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalInt;

public record HudRender(boolean shouldRender, ResourceLocation spriteLocation, int barIndex, int iconIndex,
                        EntityCondition condition, boolean inverted, OptionalInt order) {
    public static final Codec<HudRender> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("should_render", true).forGetter(HudRender::shouldRender),
            ResourceLocation.CODEC.optionalFieldOf("sprite_location", ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "textures/gui/resource_bar.png")).forGetter(HudRender::spriteLocation),
            Codec.INT.optionalFieldOf("bar_index", 0).forGetter(HudRender::barIndex),
            Codec.INT.optionalFieldOf("icon_index", 0).forGetter(HudRender::iconIndex),
            EntityCondition.CODEC.fieldOf("condition").forGetter(HudRender::condition),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(HudRender::inverted),
            OptionalCodecs.integer("order").forGetter(HudRender::order)
    ).apply(i, HudRender::new));
}
