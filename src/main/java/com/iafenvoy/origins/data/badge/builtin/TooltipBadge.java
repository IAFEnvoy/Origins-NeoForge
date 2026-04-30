package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ComponentCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TooltipBadge(ResourceLocation sprite, Component text) implements Badge {
    public static final MapCodec<TooltipBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(TooltipBadge::sprite),
            ComponentCodec.TRANSLATE_FIRST.optionalFieldOf("text", Component.empty()).forGetter(TooltipBadge::text)
    ).apply(i, TooltipBadge::new));

    @Override
    public @NotNull MapCodec<? extends Badge> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation spriteId() {
        return this.sprite;
    }

    @Override
    public List<ClientTooltipComponent> getTooltipComponents(Power power, Font textRenderer, int widthLimit, float delta) {
        return List.of(ClientTooltipComponent.create(this.text.getVisualOrderText()));
    }
}
