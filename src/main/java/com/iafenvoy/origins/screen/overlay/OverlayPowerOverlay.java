package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.OverlayPower;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class OverlayPowerOverlay implements LayeredDraw.Layer {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final OverlayPower.DrawPhase phase;

    public OverlayPowerOverlay(OverlayPower.DrawPhase phase) {
        this.phase = phase;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        boolean hideGui = this.minecraft.options.hideGui;
        boolean isFirstPerson = this.minecraft.options.getCameraType().isFirstPerson();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        OriginDataHolder.get(this.minecraft.getCameraEntity()).streamActivePowers(OverlayPower.class)
                .filter(x -> x.getDrawPhase() == this.phase && (!x.shouldHideWithHud() || !hideGui) && (x.isVisibleInThirdPerson() || isFirstPerson))
                .forEach(power -> this.renderPower(power, graphics, width, height));
    }

    private void renderPower(OverlayPower power, GuiGraphics graphics, int width, int height) {
        float strength = power.getStrength();
        ColorSettings color = power.getColor();
        OverlayPower.DrawMode mode = power.getDrawMode();
        graphics.pose().pushPose();
        graphics.pose().translate((float) width / 2, (float) height / 2, 1);
        if (mode == OverlayPower.DrawMode.NAUSEA) {
            float s = Mth.lerp(strength, 2, 1);
            graphics.pose().scale(s, s, s);
        }
        graphics.pose().translate((float) -width / 2, (float) -height / 2, 0);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        float alpha = 1;
        switch (mode) {
            case NAUSEA ->
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            case TEXTURE -> {
                RenderSystem.defaultBlendFunc();
                alpha = strength;
            }
        }
        graphics.setColor(color.r().orElse(1f), color.g().orElse(1f), color.b().orElse(1f), alpha);
        graphics.blit(power.getTexture(), 0, 0, -90, 0, 0, width, height, width, height);
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        graphics.pose().popPose();
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "above_overlay"), new OverlayPowerOverlay(OverlayPower.DrawPhase.ABOVE_HUD));
        event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "below_overlay"), new OverlayPowerOverlay(OverlayPower.DrawPhase.BELOW_HUD));
    }
}
