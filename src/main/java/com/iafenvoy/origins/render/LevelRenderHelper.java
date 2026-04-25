package com.iafenvoy.origins.render;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyBlockRenderPower;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyFluidRenderPower;
import com.iafenvoy.origins.network.payload.ReloadLevelRendererS2CPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public final class LevelRenderHelper {
    private static final List<ModifyBlockRenderPower> BLOCK_RENDER_POWERS = new LinkedList<>();
    private static final List<ModifyFluidRenderPower> FLUID_RENDER_POWERS = new LinkedList<>();
    private static boolean SHOULD_RELOAD_LEVEL_RENDERER;

    public static void sendReloadPayload(Entity entity) {
        if (entity instanceof ServerPlayer player)
            PacketDistributor.sendToPlayer(player, ReloadLevelRendererS2CPayload.INSTANCE);
    }

    public static void reload() {
        SHOULD_RELOAD_LEVEL_RENDERER = true;
        BLOCK_RENDER_POWERS.clear();
        FLUID_RENDER_POWERS.clear();
    }

    public static boolean shouldReload(Entity entity) {
        if (!SHOULD_RELOAD_LEVEL_RENDERER) return false;
        SHOULD_RELOAD_LEVEL_RENDERER = false;
        if (entity != null) {
            OriginDataHolder holder = OriginDataHolder.get(entity);
            holder.streamActivePowers(ModifyBlockRenderPower.class).forEach(BLOCK_RENDER_POWERS::add);
            holder.streamActivePowers(ModifyFluidRenderPower.class).forEach(FLUID_RENDER_POWERS::add);
        }
        return true;
    }

    public static Stream<ModifyBlockRenderPower> streamBlockRenderPowers() {
        return BLOCK_RENDER_POWERS.stream();
    }

    public static Stream<ModifyFluidRenderPower> streamFluidRenderPowers() {
        return FLUID_RENDER_POWERS.stream();
    }
}
