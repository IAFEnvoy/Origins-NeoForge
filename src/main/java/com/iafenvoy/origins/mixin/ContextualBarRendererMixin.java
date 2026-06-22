package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.data.power.builtin.regular.StatusBarTexturePower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Applies status-bar texture powers to contextual experience and mount-jump bars. */
@Mixin({ExperienceBarRenderer.class, JumpableVehicleBarRenderer.class})
public abstract class ContextualBarRendererMixin {
    @ModifyArg(
            method = "extractBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"),
            index = 1
    )
    private Identifier origins$replaceBarSprite(Identifier original) {
        return StatusBarTexturePower.replaceTexture(Minecraft.getInstance().player, original);
    }

    @ModifyArg(
            method = "extractBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"),
            index = 1,
            require = 0
    )
    private Identifier origins$replaceBarProgressSprite(Identifier original) {
        return StatusBarTexturePower.replaceTexture(Minecraft.getInstance().player, original);
    }
}
