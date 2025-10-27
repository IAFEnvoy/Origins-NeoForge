package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record Origin(List<Holder<Power>> powers, Optional<ItemStack> icon, boolean unchoosable, int order,
                     Impact impact, Optional<Component> name, Optional<Component> description, List<Upgrade> upgrades) {
    public static final Codec<Origin> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            Power.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(Origin::powers),
            ItemStack.CODEC.optionalFieldOf("icon").forGetter(Origin::icon),
            Codec.BOOL.optionalFieldOf("unchoosable", false).forGetter(Origin::unchoosable),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Origin::order),
            Impact.CODEC.optionalFieldOf("impact", Impact.NONE).forGetter(Origin::impact),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Origin::name),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(Origin::description),
            Upgrade.CODEC.listOf().optionalFieldOf("upgrades", List.of()).forGetter(Origin::upgrades)
    ).apply(i, Origin::new));
    public static final Codec<Holder<Origin>> CODEC = RegistryFixedCodec.create(OriginRegistries.ORIGIN_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Origin>> STREAM_CODEC = ByteBufCodecs.holderRegistry(OriginRegistries.ORIGIN_KEY);
    public static final Origin EMPTY = special(null, Impact.NONE, 0);

    public static Origin special(@Nullable ItemStack icon, Impact impact, int order) {
        return new Origin(List.of(), Optional.ofNullable(icon), true, order, impact, Optional.empty(), Optional.empty(), List.of());
    }

    public boolean choosable() {
        return !this.unchoosable;
    }

    public record Upgrade(ResourceLocation condition, ResourceLocation origin, Optional<Component> announcement) {
        public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("condition").forGetter(Upgrade::condition),
                ResourceLocation.CODEC.fieldOf("origin").forGetter(Upgrade::origin),
                ComponentSerialization.CODEC.optionalFieldOf("announcement").forGetter(Upgrade::announcement)
        ).apply(i, Upgrade::new));
    }
}
