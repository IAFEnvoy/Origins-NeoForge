package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.util.TextAlignment;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ScrollingTextWidget extends AbstractStringWidget {
    private TextAlignment textAlignment = TextAlignment.CENTER;
    private final boolean hasShadow;

    public ScrollingTextWidget(int x, int y, int width, int height, Component text, boolean hasShadow, Font textRenderer) {
        super(x, y, width, height, text, textRenderer);
        this.hasShadow = hasShadow;
    }

    public void setAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        int left = this.getX() + 2;
        int right = this.getX() + this.getWidth() - 2;
        int top = this.getY();
        int bottom = this.getY() + this.getHeight();

        drawScrollingText(context, this.getFont(), this.getMessage(), this.textAlignment, left, top, right, bottom, this.getColor(), this.hasShadow);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    protected static void drawScrollingText(GuiGraphics context, Font textRenderer, Component text, TextAlignment textAlignment, int left, int top, int right, int bottom, int color, boolean hasShadow) {
        int textWidth = textRenderer.width(text);
        int height = (top + bottom - 9) / 2 + 1;
        int width = right - left;

        Optional<Integer> horizontalAlignment = textAlignment.horizontal(left, right, textWidth);
        if (textWidth <= width && horizontalAlignment.isPresent())
            context.drawString(textRenderer, text, horizontalAlignment.get(), height, color, hasShadow);
        else {
            int horizontalDiff = textWidth - width;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) horizontalDiff * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, horizontalDiff);

            context.enableScissor(left, top, right, bottom);
            context.drawString(textRenderer, text, left - (int) g, height, color, hasShadow);
            context.disableScissor();
        }
    }
}
