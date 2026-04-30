package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.badge.builtin.KeybindBadge;
import net.minecraft.resources.ResourceLocation;

public final class PresetBadges {
    public static final Badge ACTIVE = new KeybindBadge(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "textures/gui/badge/active.png"), "origins.gui.badge.active");
    public static final Badge TOGGLE = new KeybindBadge(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "textures/gui/badge/toggle.png"), "origins.gui.badge.toggle");
}
