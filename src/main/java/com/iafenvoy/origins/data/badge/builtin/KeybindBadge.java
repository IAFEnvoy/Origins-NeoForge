package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record KeybindBadge(ResourceLocation sprite, String text) implements Badge {
    public static final MapCodec<KeybindBadge> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(KeybindBadge::sprite),
            Codec.STRING.optionalFieldOf("text", "").forGetter(KeybindBadge::text)
    ).apply(i, KeybindBadge::new));

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
        if (power instanceof Toggleable toggleable) {
            KeyMapping key = KeyMapping.ALL.get(toggleable.getKey().key());
            return List.of(ClientTooltipComponent.create(Component.translatable(this.text, Component.literal("[").append(key.getKey().getDisplayName()).append("]")).getVisualOrderText()));
        }
        return Badge.super.getTooltipComponents(power, textRenderer, widthLimit, delta);
    }
}
