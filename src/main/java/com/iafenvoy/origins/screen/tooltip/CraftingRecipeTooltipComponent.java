package com.iafenvoy.origins.screen.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 一个用于合成配方徽章的 {@link ClientTooltipComponent}。
 * 在工具提示中绘制 3x3 合成配方的快照。
 */
public class CraftingRecipeTooltipComponent implements ClientTooltipComponent, TooltipComponent {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("origins", "textures/gui/tooltip/recipe_tooltip.png");

    private final NonNullList<ItemStack> inputs;
    private final ItemStack output;
    private final int recipeWidth;

    public CraftingRecipeTooltipComponent(int recipeWidth, NonNullList<ItemStack> inputs, ItemStack output) {
        this.recipeWidth = recipeWidth;
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return 68;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return 130;
    }

    @Override
    public void extractImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphicsExtractor graphics) {
        this.drawBackground(graphics, x, y);
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 3; ++row) {
                int index = column + row * this.recipeWidth;
                int slotX = x + 8 + column * 18;
                int slotY = y + 8 + row * 18;
                ItemStack stack = column >= this.recipeWidth ? ItemStack.EMPTY : this.inputs.get(index);
                graphics.item(stack, slotX, slotY);
                graphics.itemDecorations(font, stack, slotX, slotY);
            }
        }
        graphics.item(this.output, x + 101, y + 25);
        graphics.itemDecorations(font, this.output, x + 101, y + 25);
    }

    private void drawBackground(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, 130, 86, 256, 256);
    }
}
