package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data.power.HudRenderable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@EventBusSubscriber(Dist.CLIENT)
public enum HudRenderOverlay implements GuiLayer {
    INSTANCE;

    // 精灵表尺寸为 256x256，这是旧版（无纹理尺寸参数）GuiGraphics#blit 所假设的。
    private static final int TEXTURE_SIZE = 256;

    @Override
    public void render(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (minecraft.options.hideGui || player == null) return;
        OriginDataHolder holder = OriginDataHolder.get(player);

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 20 + OriginsConfig.INSTANCE.general.hudOffsetX.getValue();
        int y = minecraft.getWindow().getGuiScaledHeight() - 47 + OriginsConfig.INSTANCE.general.hudOffsetY.getValue();
        if (player.getVehicle() instanceof LivingEntity vehicle) y -= 8 * (int) (vehicle.getMaxHealth() / 20f);
        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply())
            y -= 8;
        int barWidth = 71;
        int barHeight = 8;
        int iconSize = 8;

        for (HudRenderable h : holder.streamPowers(HudRenderable.class).filter(h -> h.getHudRenderData().isPresent()).sorted(Comparator.comparingInt(h -> h.getHudRenderData().get().order())).toList()) {
            HudRender render = h.getHudRenderData().orElse(null);
            if (render == null || !render.shouldRenderInActive() && !h.shouldRender(holder) || !render.condition().test(player))
                continue;
            //渲染
            Identifier currentLocation = render.spriteLocation();
            graphics.blit(RenderPipelines.GUI_TEXTURED, currentLocation, x, y, 0.0F, 0.0F, barWidth, 5, TEXTURE_SIZE, TEXTURE_SIZE);
            int v = 8 + render.barIndex() * 10;
            float fill = h.getRenderPercentage(holder);
            if (render.inverted()) fill = 1f - fill;
            int w = (int) (fill * barWidth);
            graphics.blit(RenderPipelines.GUI_TEXTURED, currentLocation, x, y - 2, 0.0F, (float) v, w, barHeight, TEXTURE_SIZE, TEXTURE_SIZE);
            graphics.blit(RenderPipelines.GUI_TEXTURED, currentLocation, x - iconSize - 2, y - 2, 73.0F, (float) v, iconSize, iconSize, TEXTURE_SIZE, TEXTURE_SIZE);
            y -= 8;
        }
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Identifier.fromNamespaceAndPath(Origins.MOD_ID, "overlay"), INSTANCE);
    }
}
