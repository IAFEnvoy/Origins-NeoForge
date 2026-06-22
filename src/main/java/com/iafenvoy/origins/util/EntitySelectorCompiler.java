package com.iafenvoy.origins.util;

import com.iafenvoy.origins.Origins;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntitySelectorCompiler {
    private static final EntitySelector DEFAULT;
    private static final Map<String, EntitySelector> CACHE = new LinkedHashMap<>();

    public static EntitySelector compile(String selector) {
        return CACHE.computeIfAbsent(selector, s -> {
            try {
                return new EntitySelectorParser(new StringReader(s), true).parse();
            } catch (Exception e) {
                Origins.LOGGER.error("Failed to compile EntitySelector {}", selector, e);
                return DEFAULT;
            }
        });
    }

    static {
        EntitySelector selector = null;
        try {
            selector = new EntitySelectorParser(new StringReader("@a[distance=...0]"), true).parse();
        } catch (Exception e) {
            Origins.LOGGER.error("Failed to compile default EntitySelector", e);
        }
        DEFAULT = selector;
    }
}
