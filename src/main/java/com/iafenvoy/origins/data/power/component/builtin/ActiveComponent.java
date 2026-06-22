package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

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
        // 一些魔法黑科技
        this.tick(holder, new PowerHolder(Identifier.withDefaultNamespace(""), power));
    }

    @Override
    public void tick(OriginDataHolder holder, PowerHolder parent) {
        Power power = parent.power();
        boolean result = power.getSettings().condition().test(holder.getEntity());
        if (result ^ this.lastActive) {
            if (result) power.active(holder);
            else power.inactive(holder);
            this.lastActive = result;
            this.markDirty();
        }
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }
}
