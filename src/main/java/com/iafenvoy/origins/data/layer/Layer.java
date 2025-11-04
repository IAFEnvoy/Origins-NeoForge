package com.iafenvoy.origins.data.layer;

import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.util.RLHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record Layer(int order, TagKey<Origin> origins, boolean enabled, Optional<GuiTitle> guiTitle,
                    boolean allowRandom, boolean allowRandomUnchoosable, List<Holder<Origin>> excludeRandom,
                    Optional<ResourceLocation> defaultOrigin, boolean autoChoose,
                    boolean hidden) implements Comparable<Layer> {
    public static final Codec<Layer> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Layer::order),
            TagKey.codec(OriginRegistries.ORIGIN_KEY).fieldOf("origins").forGetter(Layer::origins),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(Layer::enabled),
            GuiTitle.CODEC.optionalFieldOf("gui_title").forGetter(Layer::guiTitle),
            Codec.BOOL.optionalFieldOf("allow_random", false).forGetter(Layer::allowRandom),
            Codec.BOOL.optionalFieldOf("allow_random_unchoosable", false).forGetter(Layer::allowRandomUnchoosable),
            Origin.CODEC.listOf().optionalFieldOf("exclude_random", List.of()).forGetter(Layer::excludeRandom),
            ResourceLocation.CODEC.optionalFieldOf("default_origin").forGetter(Layer::defaultOrigin),
            Codec.BOOL.optionalFieldOf("auto_choose", false).forGetter(Layer::autoChoose),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Layer::hidden)
    ).apply(i, Layer::new));
    public static final Codec<Holder<Layer>> CODEC = RegistryFixedCodec.create(LayerRegistries.LAYER_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Layer>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LayerRegistries.LAYER_KEY);

    public int getOriginOptionCount(RegistryAccess access) {
        List<Holder<Origin>> choosableOrigins = this.collectChoosableOrigins(access).toList();
        int count = choosableOrigins.size();
        if (count > 1 && (this.allowRandom && choosableOrigins.stream().anyMatch(x -> !this.excludeRandom.contains(x))))
            count++;
        return count;
    }

    public Stream<Holder<Origin>> collectOrigins(RegistryAccess access) {
        return access.registryOrThrow(OriginRegistries.ORIGIN_KEY).getOrCreateTag(this.origins).stream();
    }

    public Stream<Holder<Origin>> collectChoosableOrigins(RegistryAccess access) {
        return this.collectOrigins(access).filter(x -> x.value().choosable());
    }

    public Stream<Holder<Origin>> collectRandomizableOrigins(RegistryAccess access) {
        return this.collectOrigins(access).filter(x -> !this.excludeRandom.contains(x));
    }

    public Component getChooseOriginTitle(Component fallback) {
        return this.guiTitle.flatMap(x -> x.chooseOrigin).orElse(fallback);
    }

    public Component getViewOriginTitle(Component fallback) {
        return this.guiTitle.flatMap(x -> x.viewOrigin).orElse(fallback);
    }

    @Override
    public int compareTo(@NotNull Layer that) {
        return Integer.compare(this.order, that.order);
    }

    public static MutableComponent getName(Holder<Layer> layer) {
        return getName(RLHelper.id(layer));
    }

    public static MutableComponent getName(ResourceLocation id) {
        return Component.translatable(id.toLanguageKey("layer", "name"));
    }

    public record GuiTitle(Optional<Component> chooseOrigin, Optional<Component> viewOrigin) {
        public static final Codec<GuiTitle> CODEC = RecordCodecBuilder.create(i -> i.group(
                ComponentSerialization.CODEC.optionalFieldOf("choose_origin").forGetter(GuiTitle::chooseOrigin),
                ComponentSerialization.CODEC.optionalFieldOf("view_origin").forGetter(GuiTitle::viewOrigin)
        ).apply(i, GuiTitle::new));
    }
}
