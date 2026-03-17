package com.iafenvoy.origins.screen.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ClientTooltipComponent} used for crafting recipe badges.
 * Draws a snapshot of a 3x3 crafting recipe in the tooltip.
 */
@OnlyIn(Dist.CLIENT)
public class CraftingRecipeTooltipComponent implements ClientTooltipComponent, TooltipComponent {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("origins", "textures/gui/tooltip/recipe_tooltip.png");

    private final NonNullList<ItemStack> inputs;
    private final ItemStack output;
    private final int recipeWidth;

    public CraftingRecipeTooltipComponent(int recipeWidth, NonNullList<ItemStack> inputs, ItemStack output) {
        this.recipeWidth = recipeWidth;
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public int getHeight() {
        return 68;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return 130;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics graphics) {
        this.drawBackground(graphics, x, y);
        for (int column = 0; column < 3; ++column) {
            for (int row = 0; row < 3; ++row) {
                int index = column + row * this.recipeWidth;
                int slotX = x + 8 + column * 18;
                int slotY = y + 8 + row * 18;
                ItemStack stack = column >= this.recipeWidth ? ItemStack.EMPTY : this.inputs.get(index);
                graphics.renderItem(stack, slotX, slotY);
                graphics.renderItemDecorations(font, stack, slotX, slotY);
            }
        }
        graphics.renderItem(this.output, x + 101, y + 25);
        graphics.renderItemDecorations(font, this.output, x + 101, y + 25);
    }

    private void drawBackground(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x, y, 0, 0, 130, 86, 256, 256);
    }
}
