package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Power {
    Codec<Power> DIRECT_CODEC = DefaultedCodec.registryDispatch(PowerRegistries.POWER_TYPE, Power::codec, Function.identity(), EmptyPower::new);
    Codec<Holder<Power>> CODEC = RegistryFixedCodec.create(PowerRegistries.POWER_KEY);

    @NotNull
    MapCodec<? extends Power> codec();

    default void grant(@NotNull Entity entity) {
    }

    default void revoke(@NotNull Entity entity) {
    }

    default void entityLoad(@NotNull Entity entity) {
    }

    default void entitySave(@NotNull Entity entity) {
    }

    default void tick(@NotNull Entity entity) {
    }

    default boolean hidden() {
        return false;
    }

    default ResourceLocation getId() {
        return PowerRegistries.POWER_TYPE.getKey(this.codec());
    }

    default MutableComponent getName() {
        return Component.translatable(this.getId().toLanguageKey("power", "name"));
    }

    default MutableComponent getDescription() {
        return Component.translatable(this.getId().toLanguageKey("power", "description"));
    }
}
