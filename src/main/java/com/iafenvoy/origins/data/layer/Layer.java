package com.iafenvoy.origins.data.layer;

import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.util.HolderHelper;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record Layer(Optional<Component> name, int order, ConditionedOriginList origins, boolean enabled,
                    Optional<GuiTitle> guiTitle, boolean allowRandom, boolean allowRandomUnchoosable,
                    List<Holder<Origin>> excludeRandom, Optional<ResourceLocation> defaultOrigin, boolean autoChoose,
                    boolean hidden) implements Comparable<Layer> {
    public static final Codec<Holder<Layer>> CODEC = RegistryFixedCodec.create(LayerRegistries.LAYER_KEY);
    public static final Codec<Layer> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("name").forGetter(Layer::name),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Layer::order),
            ConditionedOriginList.CODEC.fieldOf("origins").forGetter(Layer::origins),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(Layer::enabled),
            //FIXME::Flatten this
            GuiTitle.CODEC.optionalFieldOf("gui_title").forGetter(Layer::guiTitle),
            Codec.BOOL.optionalFieldOf("allow_random", false).forGetter(Layer::allowRandom),
            //FIXME::Implement this
            Codec.BOOL.optionalFieldOf("allow_random_unchoosable", false).forGetter(Layer::allowRandomUnchoosable),
            //FIXME::Also use tag in exclude random
            Origin.CODEC.listOf().optionalFieldOf("exclude_random", List.of()).forGetter(Layer::excludeRandom),
            //FIXME::Implement this
            ResourceLocation.CODEC.optionalFieldOf("default_origin").forGetter(Layer::defaultOrigin),
            Codec.BOOL.optionalFieldOf("auto_choose", false).forGetter(Layer::autoChoose),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Layer::hidden)
    ).apply(i, Layer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Layer>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LayerRegistries.LAYER_KEY);

    public int getOriginOptionCount(@NotNull Entity entity) {
        return this.getOriginOptionCount(entity.registryAccess(), entity);
    }

    public int getOriginOptionCount(RegistryAccess access, @Nullable Entity entity) {
        List<Holder<Origin>> choosableOrigins = this.collectOrigins(access, entity).filter(x -> !x.value().unchoosable()).toList();
        int count = choosableOrigins.size();
        if (count > 1 && this.allowRandom && choosableOrigins.stream().anyMatch(x -> !this.excludeRandom.contains(x)))
            count++;
        return count;
    }

    public Stream<Holder<Origin>> collectOrigins(@NotNull Entity entity) {
        return this.collectOrigins(entity.registryAccess(), entity);
    }

    public Stream<Holder<Origin>> collectOrigins(RegistryAccess access, @Nullable Entity entity) {
        return this.origins.collectOrigins(access, entity);
    }

    public Stream<Holder<Origin>> collectRandomizableOrigins(@NotNull Entity entity) {
        return this.collectRandomizableOrigins(entity.registryAccess(), entity);
    }

    public Stream<Holder<Origin>> collectRandomizableOrigins(RegistryAccess access, @Nullable Entity entity) {
        return this.collectOrigins(access, entity).filter(x -> !this.excludeRandom.contains(x));
    }

    public Component getChooseOriginTitle(Component fallback) {
        return this.guiTitle.flatMap(GuiTitle::chooseOrigin).orElse(fallback);
    }

    public Component getViewOriginTitle(Component fallback) {
        return this.guiTitle.flatMap(GuiTitle::viewOrigin).orElse(fallback);
    }

    @Override
    public int compareTo(@NotNull Layer that) {
        return Integer.compare(this.order, that.order);
    }

    public static MutableComponent getName(Holder<Layer> layer) {
        return layer.value().name.map(Component::copy).orElse(Component.translatable(HolderHelper.id(layer).toLanguageKey("layer", "name")));
    }

    public record GuiTitle(Optional<Component> chooseOrigin, Optional<Component> viewOrigin) {
        public static final Codec<GuiTitle> CODEC = RecordCodecBuilder.create(i -> i.group(
                MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("choose_origin").forGetter(GuiTitle::chooseOrigin),
                MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("view_origin").forGetter(GuiTitle::viewOrigin)
        ).apply(i, GuiTitle::new));
    }
}
