package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.common.HudRender;
import com.iafenvoy.origins.data.power.HudRenderable;
import com.iafenvoy.origins.data.power.Power;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public enum HudRenderOverlay implements LayeredDraw.Layer {
    INSTANCE;

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (minecraft.options.hideGui || player == null) return;
        OriginDataHolder holder = OriginDataHolder.get(player);

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 20 + OriginsConfig.INSTANCE.general.hudOffsetX.getValue();
        int y = minecraft.getWindow().getGuiScaledHeight() - 47 + OriginsConfig.INSTANCE.general.hudOffsetY.getValue();
        if (player.getVehicle() instanceof LivingEntity vehicle) y -= 8 * (int) (vehicle.getMaxHealth() / 20f);
        if (player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) || player.getAirSupply() < player.getMaxAirSupply())
            y -= 8;
        int barWidth = 71;
        int barHeight = 8;
        int iconSize = 8;

        for (HudRenderable h : holder.streamPowers(HudRenderable.class).filter(h -> h.getHudRenderData().isPresent()).sorted(Comparator.comparingInt(h -> h.getHudRenderData().get().order())).toList()) {
            Power power = h.getPowerForHudRender();
            HudRender render = h.getHudRenderData().orElse(null);
            if (render == null || !render.shouldRenderInActive() && !power.isActive(holder) || !render.condition().test(player))
                continue;
            //Rendering
            ResourceLocation currentLocation = render.spriteLocation();
            graphics.blit(currentLocation, x, y, 0, 0, barWidth, 5);
            int v = 8 + render.barIndex() * 10;
            float fill = h.getRenderPercentage(holder);
            if (render.inverted()) fill = 1f - fill;
            int w = (int) (fill * barWidth);
            graphics.blit(currentLocation, x, y - 2, 0, v, w, barHeight);
            // this.setBlitOffset(this.getBlitOffset() + 1);
            graphics.blit(currentLocation, x - iconSize - 2, y - 2, 73, v, iconSize, iconSize);
            // this.setBlitOffset(this.getBlitOffset() - 1);
            y -= 8;
        }
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "overlay"), INSTANCE);
    }
}
