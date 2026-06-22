package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class WaitForNextLayerScreen extends Screen {
    private final List<Holder<Layer>> layers;
    private final int currentLayerIndex;
    private final boolean showBackground;

    public WaitForNextLayerScreen(List<Holder<Layer>> layers, int currentLayerIndex, boolean showBackground) {
        super(Component.empty());
        this.layers = List.copyOf(layers);
        this.currentLayerIndex = currentLayerIndex;
        this.showBackground = showBackground;
    }

    @Override
    protected void init() {
        Player player = Minecraft.getInstance().player;
        if (player != null && this.layers.get(this.currentLayerIndex).value().getOriginOptionCount(player) == 0) this.openSelection();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.showBackground) super.extractBackground(graphics, mouseX, mouseY, delta);
        else this.extractTransparentBackground(graphics);
    }

    public void openSelection() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            OriginDataHolder holder = OriginDataHolder.get(client.player);
            for (int index = this.currentLayerIndex + 1; index < this.layers.size(); index++) {
                Holder<Layer> layer = this.layers.get(index);
                if (!holder.hasOriginInLayer(layer) && layer.value().collectOrigins(client.player).findAny().isPresent()) {
                    client.setScreen(new ChooseOriginScreen(this.layers, index, this.showBackground));
                    return;
                }
            }
        }
        client.setScreen(null);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
