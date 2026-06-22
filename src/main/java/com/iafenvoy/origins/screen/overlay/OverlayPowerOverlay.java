package com.iafenvoy.origins.screen.overlay;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.builtin.regular.OverlayPower;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(Dist.CLIENT)
public class OverlayPowerOverlay implements GuiLayer {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final OverlayPower.DrawPhase phase;

    public OverlayPowerOverlay(OverlayPower.DrawPhase phase) {
        this.phase = phase;
    }

    @Override
    public void render(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
        Entity cameraEntity = this.minecraft.getCameraEntity();
        if (cameraEntity == null)
            return;
        boolean hideGui = this.minecraft.options.hideGui;
        boolean isFirstPerson = this.minecraft.options.getCameraType().isFirstPerson();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        OriginDataHolder.get(cameraEntity).streamActivePowers(OverlayPower.class)
                .filter(x -> x.getDrawPhase() == this.phase && (!x.shouldHideWithHud() || !hideGui)
                        && (x.isVisibleInThirdPerson() || isFirstPerson))
                .forEach(power -> this.renderPower(power, graphics, width, height));
    }

    private void renderPower(OverlayPower power, GuiGraphicsExtractor graphics, int width, int height) {
        float strength = power.getStrength();
        ColorSettings color = power.getColor();
        OverlayPower.DrawMode mode = power.getDrawMode();
        float alpha = mode == OverlayPower.DrawMode.TEXTURE ? strength : 1f;
        int tint = ARGB.colorFromFloat(alpha, color.r().orElse(1f), color.g().orElse(1f), color.b().orElse(1f));
        var pipeline = mode == OverlayPower.DrawMode.NAUSEA ? RenderPipelines.GUI_NAUSEA_OVERLAY : RenderPipelines.GUI_TEXTURED;

        graphics.pose().pushMatrix();
        if (mode == OverlayPower.DrawMode.NAUSEA) {
            float s = Mth.lerp(strength, 2, 1);
            graphics.pose().translate(width / 2f, height / 2f);
            graphics.pose().scale(s, s);
            graphics.pose().translate(-width / 2f, -height / 2f);
        }
        graphics.blit(pipeline, power.getTexture(), 0, 0, 0.0F, 0.0F, width, height, width, height,
                tint);
        graphics.pose().popMatrix();
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "above_overlay"),
                new OverlayPowerOverlay(OverlayPower.DrawPhase.ABOVE_HUD));
        event.registerBelowAll(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "below_overlay"),
                new OverlayPowerOverlay(OverlayPower.DrawPhase.BELOW_HUD));
    }
}
