package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.util.math.TextAlignment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ScrollingTextWidget extends StringWidget {
    private TextAlignment textAlignment = TextAlignment.CENTER;

    public ScrollingTextWidget(int x, int y, int width, int height, Component text, boolean hasShadow, Font textRenderer) {
        super(x, y, width, height, text, textRenderer);
    }

    public void setAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    @Override
    public void visitLines(ActiveTextCollector output) {
        Component message = this.getMessage();
        Font font = this.getFont();
        int textWidth = font.width(message);
        int left = this.getX() + 2;
        int right = this.getX() + this.getWidth() - 2;
        int top = this.getY();
        int bottom = this.getY() + this.getHeight();
        int width = right - left;
        int centerX = this.getX() + this.getWidth() / 2;

        Optional<Integer> horizontalAlignment = this.textAlignment.horizontal(left, right, textWidth);
        if (textWidth <= width && horizontalAlignment.isPresent()) {
            output.accept(horizontalAlignment.get(), (top + bottom - 9) / 2 + 1, message);
        } else {
            output.acceptScrolling(message, centerX, left, right, top, bottom);
        }
    }
}
