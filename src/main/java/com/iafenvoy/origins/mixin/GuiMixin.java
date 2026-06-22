package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.StatusBarTexturePower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Applies status-bar texture powers to the 26.1 GUI extraction pipeline. */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyArg(
            method = {"extractArmor", "extractFood", "extractAirBubbles", "extractHeart", "extractVehicleHealth", "extractCrosshair"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"),
            index = 1
    )
    private static Identifier origins$replaceHudSprite(Identifier original) {
        return StatusBarTexturePower.replaceTexture(Minecraft.getInstance().player, original);
    }

    @ModifyArg(
            method = "extractCrosshair",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"),
            index = 1
    )
    private Identifier origins$replaceCrosshairProgressSprite(Identifier original) {
        return StatusBarTexturePower.replaceTexture(Minecraft.getInstance().player, original);
    }
}
