package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.iafenvoy.origins.util.RLHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record Origin(Optional<Component> name, Optional<Component> description,
                     List<Either<Holder<Power>, TagKey<Power>>> powers, Optional<ItemStack> icon, boolean unchoosable,
                     int order, Impact impact, List<Upgrade> upgrades) implements Comparable<Origin> {
    public static final Codec<Origin> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("name").forGetter(Origin::name),
            MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("description").forGetter(Origin::description),
            RegistryCodecs.holderOrTag(PowerRegistries.POWER_KEY).listOf().optionalFieldOf("powers", List.of()).forGetter(Origin::powers),
            ItemStack.CODEC.optionalFieldOf("icon").forGetter(Origin::icon),
            Codec.BOOL.optionalFieldOf("unchoosable", false).forGetter(Origin::unchoosable),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Origin::order),
            Impact.CODEC.optionalFieldOf("impact", Impact.NONE).forGetter(Origin::impact),
            Upgrade.CODEC.listOf().optionalFieldOf("upgrades", List.of()).forGetter(Origin::upgrades)
    ).apply(i, Origin::new));
    public static final Codec<Holder<Origin>> CODEC = RegistryFixedCodec.create(OriginRegistries.ORIGIN_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Origin>> STREAM_CODEC = ByteBufCodecs.holderRegistry(OriginRegistries.ORIGIN_KEY);
    public static final Origin EMPTY = special(RLHelper.EMPTY, null, Impact.NONE, 0);

    public static Origin special(ResourceLocation id, @Nullable ItemStack icon, Impact impact, int order) {
        return new Origin(Optional.of(Component.translatable(id.toLanguageKey("origin", "name"))), Optional.of(Component.translatable(id.toLanguageKey("origin", "description"))), List.of(), Optional.ofNullable(icon), true, order, impact, List.of());
    }

    @Override
    public int compareTo(@NotNull Origin that) {
        return Integer.compare(this.order, that.order);
    }

    public static MutableComponent getName(Holder<Origin> origin) {
        return origin.value().name.map(Component::copy).orElseGet(() -> Component.translatable(RLHelper.id(origin).toLanguageKey("origin", "name")));
    }

    public static MutableComponent getDescription(Holder<Origin> origin) {
        return origin.value().description.map(Component::copy).orElseGet(() -> Component.translatable(RLHelper.id(origin).toLanguageKey("origin", "description")));
    }

    public record Upgrade(ResourceLocation condition, Holder<Origin> origin, Optional<Component> announcement) {
        public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("condition").forGetter(Upgrade::condition),
                Origin.CODEC.fieldOf("origin").forGetter(Upgrade::origin),
                MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("announcement").forGetter(Upgrade::announcement)
        ).apply(i, Upgrade::new));
    }
}
