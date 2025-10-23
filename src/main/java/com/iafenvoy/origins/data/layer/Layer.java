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
                    Optional<GuiTitle> gui_title, Optional<Component> missing_name,
                    Optional<Component> missing_description, boolean allow_random, boolean allow_random_unchoosable,
                    List<ResourceLocation> exclude_random, Optional<ResourceLocation> default_origin,
                    boolean auto_choose, boolean hidden) {
    public static final Codec<Layer> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Layer::order),
            TagKey.codec(OriginRegistries.ORIGIN_KEY).fieldOf("origins").forGetter(Layer::origins),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(Layer::enabled),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Layer::name),
            GuiTitle.CODEC.optionalFieldOf("gui_title").forGetter(Layer::gui_title),
            ComponentSerialization.CODEC.optionalFieldOf("missing_name").forGetter(Layer::missing_name),
            ComponentSerialization.CODEC.optionalFieldOf("missing_description").forGetter(Layer::missing_description),
            Codec.BOOL.optionalFieldOf("allow_random", false).forGetter(Layer::allow_random),
            Codec.BOOL.optionalFieldOf("allow_random_unchoosable", false).forGetter(Layer::allow_random_unchoosable),
            ResourceLocation.CODEC.listOf().optionalFieldOf("exclude_random", List.of()).forGetter(Layer::exclude_random),
            ResourceLocation.CODEC.optionalFieldOf("default_origin").forGetter(Layer::default_origin),
            Codec.BOOL.optionalFieldOf("auto_choose", false).forGetter(Layer::auto_choose),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Layer::hidden)
    ).apply(i, Layer::new));

    public record GuiTitle(Optional<Component> choose_origin, Optional<Component> view_origin) {
        public static final Codec<GuiTitle> CODEC = RecordCodecBuilder.create(i -> i.group(
                ComponentSerialization.CODEC.optionalFieldOf("choose_origin").forGetter(GuiTitle::choose_origin),
                ComponentSerialization.CODEC.optionalFieldOf("view_origin").forGetter(GuiTitle::view_origin)
        ).apply(i, GuiTitle::new));
    }
}
