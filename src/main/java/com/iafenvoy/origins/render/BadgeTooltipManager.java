package com.iafenvoy.origins.render;

import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BadgeTooltipManager {
    private static final Map<MapCodec<? extends Badge>, Provider<Badge>> PROVIDERS = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Badge> void register(MapCodec<T> codec, Provider<T> provider) {
        PROVIDERS.put(codec, (Provider<Badge>) provider);
    }

    public static List<ClientTooltipComponent> getTooltipComponents(Badge badge, Power power, Font font, int widthLimit, float delta) {
        for (Map.Entry<MapCodec<? extends Badge>, Provider<Badge>> entry : PROVIDERS.entrySet())
            if (Objects.equals(entry.getKey(), badge.codec()))
                return entry.getValue().getTooltipComponents(badge, power, font, widthLimit, delta);
        return List.of();
    }

    public interface Provider<T extends Badge> {
        List<ClientTooltipComponent> getTooltipComponents(T badge, Power power, Font font, int widthLimit, float delta);
    }
}
