package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WaitForNextLayerScreen extends Screen {
    private final List<Holder<Layer>> layerList;
    private final int currentLayerIndex;
    private final boolean showDirtBackground;
    private final int maxSelection;

    public WaitForNextLayerScreen(List<Holder<Layer>> layerList, int currentLayerIndex, boolean showDirtBackground) {
        super(Component.empty());
        this.layerList = layerList;
        this.currentLayerIndex = currentLayerIndex;
        this.showDirtBackground = showDirtBackground;
        Player player = Minecraft.getInstance().player;
        Holder<Layer> currentLayer = layerList.get(currentLayerIndex);
        this.maxSelection = currentLayer.value().getOriginOptionCount(player.registryAccess());
    }

    public void openSelection() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            EntityOriginAttachment component = EntityOriginAttachment.get(client.player);
            Holder<Layer> layer;
            for (int index = this.currentLayerIndex + 1; index < this.layerList.size(); index++) {
                layer = this.layerList.get(index);
                if (!component.hasOrigin(layer) && layer.value().collectOrigins(client.player.registryAccess()).findAny().isPresent()) {
                    client.setScreen(new ChooseOriginScreen(this.layerList, index, this.showDirtBackground));
                    return;
                }
            }
        }
        client.setScreen(null);

    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.maxSelection == 0) this.openSelection();
        else this.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.showDirtBackground) super.renderMenuBackground(context);
        else super.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
