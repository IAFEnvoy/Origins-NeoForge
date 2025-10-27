package com.iafenvoy.origins.data.origin;

import com.iafenvoy.origins.Origins;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public enum Impact {
    NONE(0, "none", ChatFormatting.GRAY, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/impact/none")),
    LOW(1, "low", ChatFormatting.GREEN, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/impact/low")),
    MEDIUM(2, "medium", ChatFormatting.YELLOW, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/impact/medium")),
    HIGH(3, "high", ChatFormatting.RED, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/impact/high"));
    public static final Codec<Impact> CODEC = ExtraCodecs.idResolverCodec(Impact::getImpactValue, Impact::getByValue, 0);
    private final int impactValue;
    private final String translationKey;
    private final ChatFormatting textStyle;
    private final ResourceLocation spriteId;

    Impact(int impactValue, String translationKey, ChatFormatting textStyle, ResourceLocation spriteId) {
        this.translationKey = "origins.gui.impact." + translationKey;
        this.impactValue = impactValue;
        this.textStyle = textStyle;
        this.spriteId = spriteId;
    }

    public ResourceLocation getSpriteId() {
        return this.spriteId;
    }

    public int getImpactValue() {
        return this.impactValue;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public ChatFormatting getTextStyle() {
        return this.textStyle;
    }

    public MutableComponent getTextComponent() {
        return Component.translatable(this.getTranslationKey()).withStyle(this.getTextStyle());
    }

    public static Impact getByValue(int impactValue) {
        return Impact.values()[impactValue];
    }
}
