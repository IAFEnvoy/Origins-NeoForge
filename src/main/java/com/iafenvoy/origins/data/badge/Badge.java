package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface Badge {
    Codec<Badge> CODEC = DefaultedCodec.registryDispatch(BadgeRegistries.BADGE_TYPE, Badge::codec, Function.identity(), EmptyBadge::new);

    @NotNull
    MapCodec<? extends Badge> codec();

    void execute(@NotNull LivingEntity living, @NotNull Level level, @NotNull RegistryAccess access);

    ResourceLocation spriteId();

    default List<ClientTooltipComponent> getTooltipComponents(Power power, Font textRenderer, int widthLimit, float delta) {
        return List.of();
    }

    default boolean hasTooltip() {
        return false;
    }
}
