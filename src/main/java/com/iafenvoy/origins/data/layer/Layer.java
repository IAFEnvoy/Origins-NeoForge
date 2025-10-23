package com.iafenvoy.origins.data.layer;

import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.Optional;

public record Layer(int order, TagKey<Origin> origins, boolean enabled, Optional<Component> name,
                    Optional<GuiTitle> gui_title, Optional<Component> missingName,
                    Optional<Component> missingDescription, boolean allowRandom, boolean allowRandomUnchoosable,
                    List<ResourceLocation> excludeRandom, Optional<ResourceLocation> defaultOrigin,
                    boolean autoChoose, boolean hidden) {
    public static final Codec<Layer> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Layer::order),
            TagKey.codec(OriginRegistries.ORIGIN_KEY).fieldOf("origins").forGetter(Layer::origins),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(Layer::enabled),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Layer::name),
            GuiTitle.CODEC.optionalFieldOf("gui_title").forGetter(Layer::gui_title),
            ComponentSerialization.CODEC.optionalFieldOf("missing_name").forGetter(Layer::missingName),
            ComponentSerialization.CODEC.optionalFieldOf("missing_description").forGetter(Layer::missingDescription),
            Codec.BOOL.optionalFieldOf("allow_random", false).forGetter(Layer::allowRandom),
            Codec.BOOL.optionalFieldOf("allow_random_unchoosable", false).forGetter(Layer::allowRandomUnchoosable),
            ResourceLocation.CODEC.listOf().optionalFieldOf("exclude_random", List.of()).forGetter(Layer::excludeRandom),
            ResourceLocation.CODEC.optionalFieldOf("default_origin").forGetter(Layer::defaultOrigin),
            Codec.BOOL.optionalFieldOf("auto_choose", false).forGetter(Layer::autoChoose),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Layer::hidden)
    ).apply(i, Layer::new));

    public record GuiTitle(Optional<Component> choose_origin, Optional<Component> view_origin) {
        public static final Codec<GuiTitle> CODEC = RecordCodecBuilder.create(i -> i.group(
                ComponentSerialization.CODEC.optionalFieldOf("choose_origin").forGetter(GuiTitle::choose_origin),
                ComponentSerialization.CODEC.optionalFieldOf("view_origin").forGetter(GuiTitle::view_origin)
        ).apply(i, GuiTitle::new));
    }
}
