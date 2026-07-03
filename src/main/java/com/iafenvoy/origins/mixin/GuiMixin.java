package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.StatusBarTexturePower;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Optional;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static Optional<StatusBarTexturePower> origins$getOverrideHudTexturePower(Player player) {
        return PowerHelper.get(player).streamActive(StatusBarTexturePower.class).findFirst();
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private static void overrideFullArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private static void overrideHalfArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private static void overrideEmptyArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void overrideEmptyFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void overrideFullFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private void overrideHalfFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        origins$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")))
    private void overrideBubbleSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")))
    private void overrideBurstingBubbleSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void overrideHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics context, Gui.HeartType type, int mX, int mY, boolean hardcore, boolean blinking, boolean half) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawHeartTexture(instance, type, x, y, width, height, hardcore, blinking, half),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void overrideBaseExperienceBarSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void overrideProgressExperienceBarSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void overrideCrosshairSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V")))
    private void overrideFullCrosshairAttackIndicatorSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V")))
    private void overrideBaseCrosshairAttackIndicatorSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void overrideCrosshairAttackIndicatorProgressSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderJumpMeter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void overrideBaseMountJumpBarSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderJumpMeter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void overrideMountJumpBarProgressSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void overrideEmptyMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void overrideFullMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private void overrideHalfMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        origins$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }
}
