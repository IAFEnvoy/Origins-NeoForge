package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.data.badge.builtin.EmptyBadge;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface Badge {
    Codec<Badge> DIRECT_CODEC = DefaultedCodec.registryDispatch(BadgeRegistries.BADGE_TYPE, Badge::codec, Function.identity(), () -> EmptyBadge.INSTANCE);
    Codec<Holder<Badge>> CODEC = RegistryFixedCodec.create(BadgeRegistries.BADGE_KEY);

    @NotNull
    MapCodec<? extends Badge> codec();

    ResourceLocation spriteId();

    default List<ClientTooltipComponent> getTooltipComponents(Power power, Font textRenderer, int widthLimit, float delta) {
        return List.of();
    }
}
