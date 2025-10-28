package com.iafenvoy.origins.screen;

import com.google.common.collect.Lists;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ViewOriginScreen extends OriginDisplayScreen {

    private final List<Pair<Holder<Layer>, Holder<Origin>>> originLayers;
    private Button chooseOriginButton;

    private int currentLayerIndex = 0;

    public ViewOriginScreen() {
        super(Component.translatable(Origins.MOD_ID + ".screen.view_origin"), false);

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            this.originLayers = new LinkedList<>();
            return;
        }

        Map<Holder<Layer>, Holder<Origin>> origins = EntityOriginAttachment.get(player).getOrigins();
        this.originLayers = new LinkedList<>();

        origins.forEach((layer, origin) -> {

            ItemStack iconStack = origin.value().icon().orElse(ItemStack.EMPTY);
            if (iconStack.is(Items.PLAYER_HEAD) && !iconStack.has(DataComponents.PROFILE)) {
                iconStack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
            }

            if (!layer.value().hidden() && (origin.value() != Origin.EMPTY || layer.value().getOriginOptionCount(player.registryAccess()) > 0)) {
                this.originLayers.add(Pair.of(layer, origin));
            }

        });

        this.originLayers.sort(Comparator.comparing(x -> x.getFirst().value()));
        if (this.originLayers.isEmpty()) {
            this.showOrigin(null, null, false);
        } else {
            Pair<Holder<Layer>, Holder<Origin>> currentOriginAndLayer = this.originLayers.get(this.currentLayerIndex);
            this.showOrigin(currentOriginAndLayer.getSecond(), currentOriginAndLayer.getFirst(), false);
        }

    }

    @Override
    protected void init() {

        super.init();
        Minecraft client = Minecraft.getInstance();

        this.addRenderableWidget(Button.builder(
                Component.translatable(Origins.MOD_ID + ".gui.close"),
                button -> client.setScreen(null)
        ).bounds(this.guiLeft + WINDOW_WIDTH / 2 - 50, this.guiTop + WINDOW_HEIGHT + 5, 100, 20).build());

        if (this.originLayers.isEmpty()) {
            return;
        }

        this.addRenderableWidget(this.chooseOriginButton = Button.builder(
                Component.translatable(Origins.MOD_ID + ".gui.choose"),
                button -> client.setScreen(new ChooseOriginScreen(Lists.newArrayList(this.getCurrentLayer()), 0, false))
        ).bounds(this.guiLeft + WINDOW_WIDTH / 2 - 50, this.guiTop + WINDOW_HEIGHT - 40, 100, 20).build());

        Player player = client.player;
        this.chooseOriginButton.active = this.chooseOriginButton.visible = this.getCurrentOrigin().value() == Origin.EMPTY && this.getCurrentLayer().value().getOriginOptionCount(player.registryAccess()) > 0;

        if (this.originLayers.size() <= 1) {
            return;
        }

        //	Draw previous layer button
        this.addRenderableWidget(Button.builder(
                Component.literal("<"),
                button -> {

                    this.currentLayerIndex = (this.currentLayerIndex - 1 + this.originLayers.size()) % this.originLayers.size();
                    this.showOrigin(this.getCurrentOrigin(), this.getCurrentLayer(), false);

                    this.chooseOriginButton.active = this.chooseOriginButton.visible = this.getCurrentOrigin().value() == Origin.EMPTY && this.getCurrentLayer().value().getOriginOptionCount(player.registryAccess()) > 0;

                }
        ).bounds(this.guiLeft - 40, this.height / 2 - 10, 20, 20).build());

        //	Draw next layer button
        this.addRenderableWidget(Button.builder(
                Component.literal(">"),
                button -> {

                    this.currentLayerIndex = (this.currentLayerIndex + 1) % this.originLayers.size();
                    this.showOrigin(this.getCurrentOrigin(), this.getCurrentLayer(), false);

                    this.chooseOriginButton.active = this.chooseOriginButton.visible = this.getCurrentOrigin().value() == Origin.EMPTY && this.getCurrentLayer().value().getOriginOptionCount(player.registryAccess()) > 0;

                }
        ).bounds(this.guiLeft + WINDOW_WIDTH + 20, this.height / 2 - 10, 20, 20).build());

    }

    @Override
    public Holder<Layer> getCurrentLayer() {
        return this.originLayers.get(this.currentLayerIndex).getFirst();
    }

    @Override
    public Holder<Origin> getCurrentOrigin() {
        return this.originLayers.get(this.currentLayerIndex).getSecond();
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (!this.originLayers.isEmpty()) {
            return;
        }

        //FIXME::??
        String translationKey = Origins.MOD_ID + ".gui.view_origin.empty";
        context.drawCenteredString(this.font, Component.translatable(translationKey), this.width / 2, this.guiTop + 48, 0xFFFFFF);

    }

    @Override
    protected Component getTitleText() {
        return super.getCurrentLayer().value().getViewOriginTitle();
    }

}