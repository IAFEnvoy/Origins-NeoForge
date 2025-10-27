package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record Origin(List<Holder<Power>> powers, Optional<ItemStack> icon, boolean unchoosable, int order, int impact,
                     Optional<Component> name, Optional<Component> description, List<Upgrade> upgrades) {
    public static final Codec<Origin> CODEC = RecordCodecBuilder.create(i -> i.group(
            Power.CODEC.listOf().optionalFieldOf("powers", List.of()).forGetter(Origin::powers),
            ItemStack.CODEC.optionalFieldOf("icon").forGetter(Origin::icon),
            Codec.BOOL.optionalFieldOf("unchoosable", false).forGetter(Origin::unchoosable),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Origin::order),
            ExtraCodecs.intRange(0, 3).optionalFieldOf("impact", 0).forGetter(Origin::impact),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Origin::name),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(Origin::description),
            Upgrade.CODEC.listOf().optionalFieldOf("upgrades", List.of()).forGetter(Origin::upgrades)
    ).apply(i, Origin::new));

    public record Upgrade(ResourceLocation condition, ResourceLocation origin, Optional<Component> announcement) {
        public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("condition").forGetter(Upgrade::condition),
                ResourceLocation.CODEC.fieldOf("origin").forGetter(Upgrade::origin),
                ComponentSerialization.CODEC.optionalFieldOf("announcement").forGetter(Upgrade::announcement)
        ).apply(i, Upgrade::new));
    }
}
