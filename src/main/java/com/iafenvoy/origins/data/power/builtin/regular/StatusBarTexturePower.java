package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StatusBarTexturePower extends Power implements Prioritized {
    public static final MapCodec<StatusBarTexturePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("texture_map").forGetter(StatusBarTexturePower::getTextureMap),
            Codec.INT.fieldOf("priority").forGetter(StatusBarTexturePower::getPriority)
    ).apply(i, StatusBarTexturePower::new));
    private final Map<ResourceLocation, ResourceLocation> textureMap;
    private final int priority;

    protected StatusBarTexturePower(BaseSettings settings, Map<ResourceLocation, ResourceLocation> textureMap, int priority) {
        super(settings);
        this.textureMap = textureMap;
        this.priority = priority;
    }

    public Map<ResourceLocation, ResourceLocation> getTextureMap() {
        return this.textureMap;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    //FIXME::Move to Mixin class
    @OnlyIn(Dist.CLIENT)
    public void drawHeartTexture(GuiGraphics context, Gui.HeartType heartType, int x, int y, int width, int height, boolean hardcore, boolean blinking, boolean half) {
        ResourceLocation texture = heartType.getSprite(hardcore, half, blinking);
        ResourceLocation newTexture = this.textureMap.getOrDefault(texture, texture);
        context.blitSprite(newTexture, x, y, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTextureRegion(GuiGraphics context, ResourceLocation texture, int width, int height, int minU, int minV, int legacyMinU, int legacyMinV, int x, int y, int maxU, int maxV) {
        context.blitSprite(this.textureMap.getOrDefault(texture, texture), width, height, minU, minV, x, y, maxU, maxV);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int legacyU, int legacyV, int width, int height) {
        context.blitSprite(this.textureMap.getOrDefault(texture, texture), x, y, width, height);
    }
}
