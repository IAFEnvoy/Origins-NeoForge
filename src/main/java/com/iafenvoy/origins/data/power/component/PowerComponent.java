package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.builtin.EmptyComponent;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class PowerComponent {
    public static final Codec<PowerComponent> CODEC = DefaultedCodec.registryDispatch(PowerComponentRegistries.POWER_COMPONENT_TYPE, PowerComponent::codec, Function.identity(), PowerComponent::createEmpty);
    private boolean dirty = false;

    private static PowerComponent createEmpty() {
        return new EmptyComponent();
    }

    @NotNull
    public abstract MapCodec<? extends PowerComponent> codec();

    public void tick(OriginDataHolder holder, ResourceLocation id) {
    }

    public boolean isDirty() {
        if (!this.dirty) return false;
        this.dirty = false;
        return true;
    }

    public void markDirty() {
        this.dirty = true;
    }
}
