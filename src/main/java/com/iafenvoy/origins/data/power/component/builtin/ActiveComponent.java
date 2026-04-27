package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class ActiveComponent extends PowerComponent {
    public static final MapCodec<ActiveComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("last_active").forGetter(ActiveComponent::isLastActive)
    ).apply(i, ActiveComponent::new));
    private boolean lastActive;

    public ActiveComponent(boolean lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isLastActive() {
        return this.lastActive;
    }

    public void tick(OriginDataHolder holder, Power power) {
        boolean result = power.isActive(holder);
        if (result ^ this.lastActive) {
            if (result) power.active(holder);
            else power.inactive(holder);
            this.lastActive = result;
        }
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }
}
