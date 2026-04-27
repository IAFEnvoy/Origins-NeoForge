package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleComponent extends PowerComponent {
    public static final MapCodec<ToggleComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("active").forGetter(ToggleComponent::isActive)
    ).apply(i, ToggleComponent::new));
    private boolean active;

    public ToggleComponent() {
    }

    public ToggleComponent(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        if (this.active ^ active) {
            this.active = active;
            this.markDirty();
        }
    }

    public void toggle() {
        this.active ^= true;
        this.markDirty();
    }

    public void sendMessage(OriginDataHolder holder, String key) {
        if (holder.getEntity() instanceof Player player)
            player.displayClientMessage(Component.translatable(key).append(": ").append(this.active ? Component.literal("ON").withColor(0xFF00FF00) : Component.literal("OFF").withColor(0xFFFF0000)), true);
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }
}
