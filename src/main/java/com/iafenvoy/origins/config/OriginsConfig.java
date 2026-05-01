package com.iafenvoy.origins.config;

import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.BooleanEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;
import com.iafenvoy.origins.Origins;
import net.minecraft.resources.ResourceLocation;

public class OriginsConfig extends AutoInitConfigContainer {
    public static final OriginsConfig INSTANCE = new OriginsConfig();
    public final General general = new General();

    public OriginsConfig() {
        super(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "config"), "config.origins.title", "./config/origins.json");
    }

    public static class General extends AutoInitConfigCategoryBase {
        public final IntegerEntry permissionLevel = IntegerEntry.builder("config.origins.general.permissionLevel", 2).key("permissionLevel").range(0, 4).build();
        public final BooleanEntry compactUsabilityHints = BooleanEntry.builder("config.origins.general.compactUsabilityHints", false).key("compactUsabilityHints").build();
        public final BooleanEntry separateSpawnFindingThread = BooleanEntry.builder("config.origins.general.separateSpawnFindingThread", false).key("separateSpawnFindingThread").build();
        public final IntegerEntry hudOffsetX = IntegerEntry.builder("config.origins.general.hudOffsetX", 0).key("hudOffsetX").build();
        public final IntegerEntry hudOffsetY = IntegerEntry.builder("config.origins.general.hudOffsetY", 0).key("hudOffsetY").build();

        public General() {
            super("general", "category.origins.general.title");
        }
    }
}
