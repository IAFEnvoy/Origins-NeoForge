package com.iafenvoy.origins.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public final class OriginsConfig {
    public static final OriginsConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        INSTANCE = new OriginsConfig(builder);
        SPEC = builder.build();
    }

    public final General general;
    public final ModifyPlayerSpawnPower modifyPlayerSpawnPower;
    public final Debug debug;

    private OriginsConfig(ModConfigSpec.Builder builder) {
        this.general = new General(builder);
        this.modifyPlayerSpawnPower = new ModifyPlayerSpawnPower(builder);
        this.debug = new Debug(builder);
    }

    public static final class Value<T> {
        private final Supplier<T> value;

        private Value(Supplier<T> value) {
            this.value = value;
        }

        public T getValue() {
            return this.value.get();
        }
    }

    public static final class General {
        public final Value<Integer> permissionLevel;
        public final Value<Boolean> compactUsabilityHints;
        public final Value<Integer> hudOffsetX;
        public final Value<Integer> hudOffsetY;

        private General(ModConfigSpec.Builder builder) {
            builder.push("general");
            this.permissionLevel = new Value<>(builder.comment("Command permission level (0-4).")
                    .defineInRange("permissionLevel", 2, 0, 4));
            this.compactUsabilityHints = new Value<>(builder.define("compactUsabilityHints", false));
            this.hudOffsetX = new Value<>(builder.define("hudOffsetX", 0));
            this.hudOffsetY = new Value<>(builder.define("hudOffsetY", 0));
            builder.pop();
        }
    }

    public static final class ModifyPlayerSpawnPower {
        public final Value<Integer> radius;
        public final Value<Integer> horizontalBlockCheckInterval;
        public final Value<Integer> verticalBlockCheckInterval;

        private ModifyPlayerSpawnPower(ModConfigSpec.Builder builder) {
            builder.push("modifyPlayerSpawnPower");
            this.radius = new Value<>(builder.defineInRange("radius", 6400, 1, Integer.MAX_VALUE));
            this.horizontalBlockCheckInterval = new Value<>(builder.defineInRange("horizontalBlockCheckInterval", 64, 0, Integer.MAX_VALUE));
            this.verticalBlockCheckInterval = new Value<>(builder.defineInRange("verticalBlockCheckInterval", 64, 0, Integer.MAX_VALUE));
            builder.pop();
        }
    }

    public static final class Debug {
        public final Value<Boolean> builtinRegistries;
        public final Value<Boolean> dynamicRegistries;

        private Debug(ModConfigSpec.Builder builder) {
            builder.push("debug");
            this.builtinRegistries = new Value<>(builder.define("builtinRegistries", false));
            this.dynamicRegistries = new Value<>(builder.define("dynamicRegistries", false));
            builder.pop();
        }
    }
}
