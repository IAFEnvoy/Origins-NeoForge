package com.iafenvoy.origins.util;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber
public final class Timeout {
    private static final CopyOnWriteArrayList<Timeout> TIMEOUTS = new CopyOnWriteArrayList<>();
    private final int waitTicks;
    private final int maxTimes;
    private final Runnable callback, finalize;
    public boolean shouldRemove = false;
    private int ticks = 0;
    private int currentTimes = 0;

    private Timeout(int waitTicks, int maxTimes, Runnable callback, Runnable finalize) {
        this.waitTicks = waitTicks;
        this.maxTimes = maxTimes;
        this.callback = callback;
        this.finalize = finalize;
    }

    public static void create(int waitTicks, Runnable callback) {
        create(waitTicks, 1, callback);
    }

    public static void create(int waitTicks, int maxTimes, Runnable callback) {
        create(waitTicks, maxTimes, callback, () -> {
        });
    }

    public static void create(int waitTicks, int maxTimes, Runnable callback, Runnable finalize) {
        if (maxTimes <= 0) return;
        TIMEOUTS.add(new Timeout(waitTicks, maxTimes, callback, finalize));
    }

    @SubscribeEvent
    public static void runTimeout(ServerTickEvent.Post event) {
        TIMEOUTS.forEach(x -> x.tick(event.getServer()));
        TIMEOUTS.removeAll(TIMEOUTS.stream().filter(timeout -> timeout.shouldRemove).toList());
    }

    public void tick(MinecraftServer server) {
        this.ticks++;
        if (this.ticks >= this.waitTicks) {
            this.ticks -= this.waitTicks;
            server.execute(this.callback);
            this.currentTimes++;
            if (this.currentTimes >= this.maxTimes) {
                this.shouldRemove = true;
                this.finalize.run();
            }
        }
    }
}
