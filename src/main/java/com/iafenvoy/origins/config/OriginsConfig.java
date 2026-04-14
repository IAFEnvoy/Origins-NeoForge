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
        public final BooleanEntry showOutput = BooleanEntry.builder("config.origins.general.showOutput", false).key("showOutput").build();

        public General() {
            super("general", "category.origins.general.title");
        }
    }
}
