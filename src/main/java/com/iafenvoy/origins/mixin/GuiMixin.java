package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.StatusBarTexturePower;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
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

    @Shadow
    protected abstract Player getCameraPlayer();

    @Unique
    private static Optional<StatusBarTexturePower> apoli$getOverrideHudTexturePower(Player player) {
        return OriginDataHolder.get(player).streamActivePowers(StatusBarTexturePower.class).findFirst();
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private static void apoli$overrideFullArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, 34, 9, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private static void apoli$overrideHalfArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, 25, 9, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private static void apoli$overrideEmptyArmorSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, 16, 9, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void apoli$overrideEmptyFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, this.getCameraPlayer().hasEffect(MobEffects.HUNGER) ? 133 : 16, 27, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void apoli$overrideFullFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, this.getCameraPlayer().hasEffect(MobEffects.HUNGER) ? 88 : 52, 27, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private void apoli$overrideHalfFoodSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics mContext, Player player) {
        apoli$getOverrideHudTexturePower(player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, this.getCameraPlayer().hasEffect(MobEffects.HUNGER) ? 97 : 61, 27, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")))
    private void apoli$overrideBubbleSprite(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(context, texture, x, y, 16, 18, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")))
    private void apoli$overrideBurstingBubbleSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 25, 18, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void apoli$overrideHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original, GuiGraphics context, Gui.HeartType type, int mX, int mY, boolean hardcore, boolean blinking, boolean half) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawHeartTexture(instance, type, x, y, width, height, hardcore, blinking, half),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void apoli$overrideBaseExperienceBarSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 0, 64, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void apoli$overrideProgressExperienceBarSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original, @Local(ordinal = 1) int experienceProgress) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, 0, 69, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void apoli$overrideCrosshairSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 0, 0, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V")))
    private void apoli$overrideFullCrosshairAttackIndicatorSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 68, 94, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V")))
    private void apoli$overrideBaseCrosshairAttackIndicatorSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 36, 94, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void apoli$overrideCrosshairAttackIndicatorProgressSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, 52, 94, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderJumpMeter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void apoli$overrideBaseMountJumpBarSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 0, 84, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderJumpMeter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private void apoli$overrideMountJumpBarProgressSprite(GuiGraphics instance, ResourceLocation texture, int i, int j, int k, int l, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTextureRegion(instance, texture, i, j, k, l, 0, 89, x, y, width, height),
                () -> original.call(instance, texture, i, j, k, l, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void apoli$overrideEmptyMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 52, 9, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void apoli$overrideFullMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 88, 9, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }

    @WrapOperation(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private void apoli$overrideHalfMountHeartSprite(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        apoli$getOverrideHudTexturePower(this.minecraft.player).ifPresentOrElse(
                p -> p.drawTexture(instance, texture, x, y, 97, 9, width, height),
                () -> original.call(instance, texture, x, y, width, height)
        );
    }
}
