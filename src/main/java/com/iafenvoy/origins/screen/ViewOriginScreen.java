package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.ItemStackReference;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ViewOriginScreen extends OriginDisplayScreen {
    private final List<Pair<Holder<Layer>, Holder<Origin>>> originLayers = new ArrayList<>();
    private Button chooseOriginButton;
    private int currentLayerIndex;

    public ViewOriginScreen() {
        super(Component.empty(), false);
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        OriginDataHolder.get(player).getOrigins().forEach((layer, origin) -> {
            if (!layer.value().hidden() && (origin.value() != Origin.EMPTY || layer.value().getOriginOptionCount(player) > 0))
                this.originLayers.add(Pair.of(layer, origin));
        });
        this.originLayers.sort(Comparator.comparing(entry -> entry.getFirst().value()));
        if (!this.originLayers.isEmpty()) this.showCurrentOrigin();
    }

    @Override
    public @NotNull Component getTitle() {
        if (this.originLayers.isEmpty()) return Component.translatable("origins.gui.view_origin.title");
        return this.getCurrentLayer().value().getViewOriginTitle(
                Component.translatable("origins.gui.view_origin.title", Layer.getName(this.getCurrentLayer()))
        );
    }

    @Override
    protected void init() {
        super.init();
        Minecraft client = Minecraft.getInstance();
        this.addRenderableWidget(Button.builder(Component.translatable(Origins.MOD_ID + ".gui.close"), button -> this.onClose())
                .bounds(this.guiLeft + WINDOW_WIDTH / 2 - 50, this.guiTop + WINDOW_HEIGHT + 5, 100, 20)
                .build());

        if (this.originLayers.isEmpty()) return;
        Player player = client.player;
        if (player == null) return;

        this.chooseOriginButton = this.addRenderableWidget(Button.builder(
                        Component.translatable(Origins.MOD_ID + ".gui.choose"),
                        button -> client.setScreen(new ChooseOriginScreen(List.of(this.getCurrentLayer()), 0, false))
                )
                .bounds(this.guiLeft + WINDOW_WIDTH / 2 - 50, this.guiTop + WINDOW_HEIGHT - 40, 100, 20)
                .build());
        this.updateChooseButton(player);

        if (this.originLayers.size() > 1) {
            this.addRenderableWidget(Button.builder(Component.literal("<"), button -> this.changeLayer(-1, player))
                    .bounds(this.guiLeft - 40, this.height / 2 - 10, 20, 20)
                    .build());
            this.addRenderableWidget(Button.builder(Component.literal(">"), button -> this.changeLayer(1, player))
                    .bounds(this.guiLeft + WINDOW_WIDTH + 20, this.height / 2 - 10, 20, 20)
                    .build());
        }
    }

    private void changeLayer(int amount, Player player) {
        this.currentLayerIndex = Math.floorMod(this.currentLayerIndex + amount, this.originLayers.size());
        this.showCurrentOrigin();
        this.updateChooseButton(player);
    }

    private void showCurrentOrigin() {
        this.showOrigin(this.getCurrentOrigin(), this.getCurrentLayer(), false);
    }

    private void updateChooseButton(Player player) {
        boolean visible = this.getCurrentOrigin().value() == Origin.EMPTY
                && this.getCurrentLayer().value().getOriginOptionCount(player) > 0;
        this.chooseOriginButton.active = visible;
        this.chooseOriginButton.visible = visible;
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        if (this.originLayers.isEmpty())
            graphics.centeredText(this.font, Component.translatable(Origins.MOD_ID + ".gui.view_origin.empty"), this.width / 2, this.guiTop + 48, 0xFFFFFFFF);
    }

    @Override
    protected void renderOriginName(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        ItemStack icon = this.getCurrentOrigin().value().icon().map(ItemStackReference::create).orElse(ItemStack.EMPTY);
        Player player = Minecraft.getInstance().player;
        if (player != null && icon.is(Items.PLAYER_HEAD) && !icon.has(DataComponents.PROFILE))
            icon.set(DataComponents.PROFILE, ResolvableProfile.createResolved(player.getGameProfile()));
        graphics.item(icon, this.guiLeft + 15, this.guiTop + 15);
    }
}
