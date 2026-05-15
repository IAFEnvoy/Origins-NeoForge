package com.iafenvoy.origins;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.function.LongSupplier;

public final class Proxies {
    public static LongSupplier TICK_COUNT = () -> {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? 0 : server.getTickCount();
    };
}
