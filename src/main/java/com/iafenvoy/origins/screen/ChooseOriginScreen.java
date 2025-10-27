package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChooseOriginScreen extends OriginDisplayScreen {
    private final List<Layer> layerList;
    private final List<Origin> originSelection;
    private final int currentLayerIndex;
    private Origin randomOrigin;
    private int currentOriginIndex = 0;
    private int maxSelection = 0;
    private static final ResourceLocation ORIGINS_CHOICES = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "textures/gui/origin_choices.png");
    private static final int CHOICES_WIDTH = 219;
    private static final int CHOICES_HEIGHT = 182;
    private static final int ORIGIN_ICON_SIZE = 26;
    private int calculatedTop;
    private int calculatedLeft;
    private int currentPage = 0;
    private static final int COUNT_PER_PAGE = 35;
    private int pages;
    private float tickTime = 0.0F;

    public ChooseOriginScreen(List<Layer> layerList, int currentLayerIndex, boolean showDirtBackground) {
        super(Component.translatable("origins.screen.choose_origin"), showDirtBackground);
        this.layerList = layerList;
        this.currentLayerIndex = currentLayerIndex;
        this.originSelection = new ArrayList(layerList.size());
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Layer currentLayer = this.getCurrentLayer();
            currentLayer.getOrigins(player).forEach((originId) -> {
                Origin origin = OriginManager.get(originId);
                if (origin.isChoosable()) {
                    ItemStack iconStack = origin.getDisplayItem();
                    if (iconStack.isOf(Items.PLAYER_HEAD) && !iconStack.contains(DataComponentTypes.PROFILE)) {
                        iconStack.set(DataComponentTypes.PROFILE, new ProfileComponent(player.getGameProfile()));
                    }

                    this.originSelection.add(origin);
                }
            });
            this.originSelection.sort(Comparator.comparingInt((o) -> o.getImpact().getImpactValue()).thenComparingInt(Origin::getOrder));
            this.maxSelection = currentLayer.getOriginOptionCount(player);
            if (this.maxSelection == 0) {
                this.openNextLayerScreen();
            }

            Origin newOrigin = this.getCurrentOrigin();
            this.showOrigin(newOrigin, this.getCurrentLayer(), newOrigin == this.randomOrigin);
        }
    }

    private void openNextLayerScreen() {
        MinecraftClient.getInstance().setScreen(new WaitForNextLayerScreen(this.layerList, this.currentLayerIndex, this.showDirtBackground));
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.handler$zbe000$altorigingui$changeGuiPosition((CallbackInfo) null);
        if (this.maxSelection > 0) {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("origins.gui.select"), (button) -> {
                Identifier originId = super.getCurrentOrigin().getId();
                Identifier layerId = this.getCurrentLayer().getId();
                if (this.currentOriginIndex == this.originSelection.size()) {
                    ClientPlayNetworking.send(new ChooseRandomOriginC2SPacket(layerId));
                } else {
                    ClientPlayNetworking.send(new ChooseOriginC2SPacket(layerId, originId));
                }

                this.openNextLayerScreen();
            }).dimensions(this.guiLeft + 88 - 50, this.guiTop + 182 + 5, 100, 20).build());
            if (this.maxSelection > 1) {
                Element injectorAllocatedLocal2 = ButtonWidget.builder(Text.of("<"), (button) -> {
                    this.currentOriginIndex = (this.currentOriginIndex - 1 + this.maxSelection) % this.maxSelection;
                    Origin newOrigin = this.getCurrentOrigin();
                    this.showOrigin(newOrigin, this.getCurrentLayer(), newOrigin == this.randomOrigin);
                }).dimensions(this.guiLeft - 40, this.height / 2 - 10, 20, 20).build();
                if (this.wrapWithCondition$zbe000$altorigingui$disableFirstArrowButton(this, injectorAllocatedLocal2)) {
                    this.addDrawableChild(injectorAllocatedLocal2);
                } else {
                    Object var10000 = null;
                }

                injectorAllocatedLocal2 = ButtonWidget.builder(Text.of(">"), (button) -> {
                    this.currentOriginIndex = (this.currentOriginIndex + 1) % this.maxSelection;
                    Origin newOrigin = this.getCurrentOrigin();
                    this.showOrigin(newOrigin, this.getCurrentLayer(), newOrigin == this.randomOrigin);
                }).dimensions(this.guiLeft + 176 + 20, this.height / 2 - 10, 20, 20).build();
                if (this.wrapWithCondition$zbe000$altorigingui$disableSecondArrowButton(this, injectorAllocatedLocal2)) {
                    this.addDrawableChild(injectorAllocatedLocal2);
                } else {
                    Object var4 = null;
                }

            }
        }
    }

    @Override
    public OriginLayer getCurrentLayer() {
        return (OriginLayer) this.layerList.get(this.currentLayerIndex);
    }

    @Override
    public Origin getCurrentOrigin() {
        if (this.currentOriginIndex == this.originSelection.size()) {
            if (this.randomOrigin == null) {
                this.initRandomOrigin();
            }

            return this.randomOrigin;
        } else {
            return (Origin) this.originSelection.get(this.currentOriginIndex);
        }
    }

    @Override
    protected Text getTitleText() {
        return super.getCurrentLayer().getChooseOriginTitle();
    }

    private void initRandomOrigin() {
        this.randomOrigin = Origin.special(Origins.identifier("random"), ModItems.ORB_OF_ORIGIN.getDefaultStack(), Impact.NONE, -1);
        MutableText randomOriginText = Text.of("").copy();
        List<Identifier> randoms = ((OriginLayer) this.layerList.get(this.currentLayerIndex)).getRandomOrigins(MinecraftClient.getInstance().player);
        randoms.sort((ia, ib) -> {
            Origin a = OriginManager.get(ia);
            Origin b = OriginManager.get(ib);
            int impactDelta = Integer.compare(a.getImpact().getImpactValue(), b.getImpact().getImpactValue());
            return impactDelta != 0 ? impactDelta : Integer.compare(a.getOrder(), b.getOrder());
        });

        for (Identifier id : randoms) {
            randomOriginText.append(OriginManager.get(id).getName());
            randomOriginText.append(Text.of("\n"));
        }

        this.setRandomOriginText(randomOriginText);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.maxSelection == 0) {
            this.openNextLayerScreen();
        } else {
            super.render(context, mouseX, mouseY, delta);
        }

        this.handler$zbe000$altorigingui$addRendering(context, mouseX, mouseY, delta, (CallbackInfo) null);
    }

    protected void handler$zbe000$altorigingui$changeGuiPosition(CallbackInfo ci) {
        this.calculatedTop = (this.height - 182) / 2;
        this.calculatedLeft = (this.width - 405) / 2;
        this.guiTop = (this.height - 182) / 2;
        this.guiLeft = this.calculatedLeft + 219 + 10;
        this.pages = (int) Math.ceil((double) ((float) this.maxSelection / 35.0F));
        int x = 0;
        int y = 0;

        for (int i = 0; i < Math.min(this.maxSelection, 35); ++i) {
            if (x > 6) {
                x = 0;
                ++y;
            }

            int actualX = 12 + x * 28 + this.calculatedLeft;
            int actualY = 10 + y * 30 + this.calculatedTop;
            this.addDrawableChild(ButtonWidget.builder(Text.of(""), (b) -> {
                int index = i + this.currentPage * 35;
                if (index <= this.maxSelection - 1) {
                    this.currentOriginIndex = index;
                    Origin newOrigin = this.getCurrentOrigin();
                    this.showOrigin(newOrigin, (OriginLayer) this.layerList.get(this.currentLayerIndex), newOrigin == this.randomOrigin);
                }
            }).position(actualX, actualY).size(26, 26).build());
            ++x;
        }

        if (this.maxSelection > 35) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("<"), (b) -> {
                --this.currentPage;
                if (this.currentPage < 0) {
                    this.currentPage = this.pages - 1;
                }

            }).position(this.calculatedLeft, this.guiTop + 182 + 5).size(20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.of(">"), (b) -> this.currentPage = (this.currentPage + 1) % this.pages).position(this.calculatedLeft + 219 - 20, this.guiTop + 182 + 5).size(20, 20).build());
        }

    }

    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    public boolean wrapWithCondition$zbe000$altorigingui$disableFirstArrowButton(ChooseOriginScreen screen, Element element) {
        return false;
    }

    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    public boolean wrapWithCondition$zbe000$altorigingui$disableSecondArrowButton(ChooseOriginScreen screen, Element element) {
        return false;
    }

    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    void handler$zbe000$altorigingui$addRendering(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.renderOriginChoicesBox(context, mouseX, mouseY, delta);
        this.tickTime += delta;
    }

    @Unique
    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    public void renderOriginChoicesBox(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(ORIGINS_CHOICES, this.calculatedLeft, this.calculatedTop, 0, 0, 219, 182);
        int x = 0;
        int y = 0;

        for (int i = this.currentPage * 35; i < Math.min((this.currentPage + 1) * 35, this.maxSelection); ++i) {
            if (x > 6) {
                x = 0;
                ++y;
            }

            int actualX = 12 + x * 28 + this.calculatedLeft;
            int actualY = 10 + y * 30 + this.calculatedTop;
            if (i >= this.originSelection.size()) {
                boolean selected = this.getCurrentOrigin().getId().equals(Origins.identifier("random"));
                this.renderRandomOrigin(context, mouseX, mouseY, delta, actualX, actualY, selected);
            } else {
                Origin origin = (Origin) this.originSelection.get(i);
                boolean selected = origin.getId().equals(this.getCurrentOrigin().getId());
                this.renderOriginWidget(context, mouseX, mouseY, delta, actualX, actualY, selected, origin);
                context.drawItem(origin.getDisplayItem(), actualX + 5, actualY + 5);
            }

            ++x;
        }

        TextRenderer var10001 = this.textRenderer;
        int var10002 = this.currentPage + 1;
        OrderedText var13 = Text.of(var10002 + "/" + this.pages).asOrderedText();
        int var10003 = this.calculatedLeft + 109;
        int var10004 = this.guiTop + 182 + 5;
        Objects.requireNonNull(this.textRenderer);
        context.drawCenteredTextWithShadow(var10001, var13, var10003, var10004 + 9 / 2, 16777215);
    }

    @Unique
    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    public void renderOriginWidget(DrawContext context, int mouseX, int mouseY, float delta, int x, int y, boolean selected, Origin origin) {
        int u;
        boolean mouseHovering;
        boolean var10000;
        label95:
        {
            label94:
            {
                RenderSystem.setShaderTexture(0, ORIGINS_CHOICES);
                u = selected ? 26 : 0;
                mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + 26 && mouseY < y + 26;
                Element var13 = this.getFocused();
                if (var13 instanceof ButtonWidget buttonWidget) {
                    if (buttonWidget.getX() == x && buttonWidget.getY() == y) {
                        break label94;
                    }
                }

                if (!mouseHovering) {
                    var10000 = false;
                    break label95;
                }
            }

            var10000 = true;
        }

        boolean guiSelected = var10000;
        if (guiSelected) {
            u += 52;
        }

        context.drawTexture(ORIGINS_CHOICES, x, y, 230, u, 26, 26);
        Impact impact = origin.impact();
        switch (impact.name()) {
            case "NONE" -> context.drawTexture(ORIGINS_CHOICES, x, y, 224, guiSelected ? 112 : 104, 8, 8);
            case "LOW" -> context.drawTexture(ORIGINS_CHOICES, x, y, 232, guiSelected ? 112 : 104, 8, 8);
            case "MEDIUM" -> context.drawTexture(ORIGINS_CHOICES, x, y, 240, guiSelected ? 112 : 104, 8, 8);
            case "HIGH" -> context.drawTexture(ORIGINS_CHOICES, x, y, 248, guiSelected ? 112 : 104, 8, 8);
            case "VERY_HIGH" -> context.drawTexture(ORIGINS_CHOICES, x, y, 248, guiSelected ? 144 : 136, 8, 8);
            default -> context.drawTexture(ORIGINS_CHOICES, x, y, 240, guiSelected ? 144 : 136, 8, 8);
        }

        if (mouseHovering) {
            Text text = this.getCurrentLayer().getName().copy().append(": ").append(origin.getName());
            context.drawTooltip(this.textRenderer, text, mouseX, mouseY);
        }

    }

    @MixinMerged(
            mixin = "me.ultrusmods.altorigingui.mixin.ChoseOriginScreenMixin",
            priority = 1000,
            sessionId = "5c79626e-ccc9-401b-a2eb-1c635671c73f"
    )
    public void renderRandomOrigin(DrawContext context, int mouseX, int mouseY, float delta, int x, int y, boolean selected) {
        int u;
        boolean var10000;
        label37:
        {
            label36:
            {
                u = selected ? 26 : 0;
                boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + 26 && mouseY < y + 26;
                Element var12 = this.getFocused();
                if (var12 instanceof ButtonWidget buttonWidget) {
                    if (buttonWidget.getX() == x && buttonWidget.getY() == y) {
                        break label36;
                    }
                }

                if (!mouseHovering) {
                    var10000 = false;
                    break label37;
                }
            }

            var10000 = true;
        }

        boolean guiSelected = var10000;
        if (guiSelected) {
            u += 52;
        }

        context.drawTexture(ORIGINS_CHOICES, x, y, 230, u, 26, 26);
        context.drawTexture(ORIGINS_CHOICES, x + 6, y + 5, 243, 120, 13, 16);
        int impact = (int) ((double) this.tickTime / (double) 15.0F) % 4;
        context.drawTexture(ORIGINS_CHOICES, x, y, 224 + impact * 8, guiSelected ? 112 : 104, 8, 8);
    }
}
