package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.badge.builtin.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BuiltinBadges {
    public static final DeferredRegister<MapCodec<? extends Badge>> REGISTRY = DeferredRegister
            .create(BadgeRegistries.BADGE_TYPE, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<EmptyBadge>> EMPTY = REGISTRY
            .register(Constants.EMPTY_KEY, () -> EmptyBadge.CODEC);
    // 列表
    public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<CraftingRecipeBadge>> CRAFTING_RECIPE = REGISTRY
            .register("crafting_recipe", () -> CraftingRecipeBadge.CODEC);
    public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<KeybindBadge>> KEYBIND = REGISTRY
            .register("keybind", () -> KeybindBadge.CODEC);
    public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<SpriteBadge>> SPRITE = REGISTRY
            .register("sprite", () -> SpriteBadge.CODEC);
    public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<TooltipBadge>> TOOLTIP = REGISTRY
            .register("tooltip", () -> TooltipBadge.CODEC);
}
