package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.component.builtin.EmptyComponent;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface PowerComponent {
    Codec<PowerComponent> CODEC = DefaultedCodec.registryDispatch(PowerComponentRegistries.POWER_COMPONENT_TYPE, PowerComponent::codec, Function.identity(), EmptyComponent::new);

    @NotNull
    MapCodec<? extends PowerComponent> codec();

    default void tick(OriginDataHolder holder, ResourceLocation id) {
    }
}
