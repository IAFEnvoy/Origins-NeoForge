package com.iafenvoy.origins.screen;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data._common.ItemStackReference;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.screen.badge.BadgeTooltipManager;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.iafenvoy.origins.util.math.TextAlignment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class OriginDisplayScreen extends Screen {
    private static final Identifier WINDOW_BACKGROUND = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/background");
    private static final Identifier WINDOW_BORDER = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/border");
    private static final Identifier WINDOW_NAME_PLATE = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/name_plate");
    private static final Identifier WINDOW_SCROLL_BAR = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar");
    private static final Identifier WINDOW_SCROLL_BAR_PRESSED = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar/pressed");
    private static final Identifier WINDOW_SCROLL_BAR_SLOT = Identifier.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar/slot");
    protected static final int WINDOW_WIDTH = 176, WINDOW_HEIGHT = 182;
    private final LinkedList<RenderedBadge> renderedBadges = new LinkedList<>();
    protected final boolean showDirtBackground;
    private Holder<Origin> origin, prevOrigin;
    private Holder<Layer> layer, prevLayer;
    private Component randomOriginText;
    protected ScrollingTextWidget originNameWidget;
    private boolean refreshOriginNameWidget = false, isOriginRandom, dragScrolling = false;
    private double mouseDragStart = 0;
    private int currentMaxScroll = 0, scrollDragStart = 0, scrollPos = 0;
    protected int guiTop, guiLeft;

    public OriginDisplayScreen(Component title, boolean showDirtBackground) {
        super(title);
        this.showDirtBackground = showDirtBackground;
    }

    public void showOrigin(Holder<Origin> origin, Holder<Layer> layer, boolean isRandom) {
        this.origin = origin;
        this.layer = layer;
        this.isOriginRandom = isRandom;
        this.scrollPos = 0;
    }

    public void setRandomOriginText(Component text) {
        this.randomOriginText = text;
    }

    @Override
    protected void init() {
        super.init();

        this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
        this.guiTop = (this.height - WINDOW_HEIGHT) / 2;

        this.originNameWidget = new ScrollingTextWidget(this.guiLeft + 38, this.guiTop + 18, WINDOW_WIDTH - (62 + 3 * 8), 9, Component.empty(), true, this.font);
        this.addRenderableWidget(this.originNameWidget);
        this.refreshOriginNameWidget = true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.showDirtBackground) super.extractBackground(graphics, mouseX, mouseY, delta);
        else this.extractTransparentBackground(graphics);
    }

    @Override
    public void extractTransparentBackground(GuiGraphicsExtractor graphics) {
        graphics.fillGradient(0, 0, this.width, this.height, 1678774288, -2112876528);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        this.renderedBadges.clear();
        if (this.origin != null && (this.refreshOriginNameWidget || this.origin != this.prevOrigin || this.layer != this.prevLayer)) {
            this.originNameWidget.setMessage(Origin.getName(this.getCurrentOrigin()));
            this.originNameWidget.setAlignment(TextAlignment.LEFT);
            this.refreshOriginNameWidget = false;
            this.prevOrigin = this.origin;
            this.prevLayer = this.layer;
        }
      
        this.renderOriginWindow(graphics, mouseX, mouseY, delta);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.dragScrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean mouseClicked = super.mouseClicked(event, doubleClick);
        if (this.cannotScroll()) return mouseClicked;

        this.dragScrolling = false;

        double mouseX = event.x();
        double mouseY = event.y();
        int scrollBarY = 36;
        int maxScrollBarOffset = 141;

        scrollBarY += (int) ((maxScrollBarOffset - scrollBarY) * (this.scrollPos / (float) this.currentMaxScroll));
        if (!this.canDragScroll(mouseX, mouseY, scrollBarY)) return mouseClicked;

        this.dragScrolling = true;
        this.scrollDragStart = scrollBarY;
        this.mouseDragStart = mouseY;

        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean mouseDragged = super.mouseDragged(event, deltaX, deltaY);
        if (!this.dragScrolling) return mouseDragged;

        int delta = (int) (event.y() - this.mouseDragStart);
        int newScrollPos = Math.max(36, Math.min(141, this.scrollDragStart + delta));

        float part = (newScrollPos - 36) / (float) (141 - 36);
        this.scrollPos = (int) (part * this.currentMaxScroll);

        return mouseDragged;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.scrollPos = Mth.clamp(this.scrollPos - (int) scrollY * 10, 0, this.currentMaxScroll);
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    public Holder<Origin> getCurrentOrigin() {
        return this.origin;
    }

    public Holder<Layer> getCurrentLayer() {
        return this.layer;
    }

    protected void renderScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.cannotScroll()) return;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_SCROLL_BAR_SLOT, this.guiLeft + 155, this.guiTop + 35, 8, 134);

        int scrollbarY = 36;
        int maxScrollbarOffset = 141;

        scrollbarY += (int) ((maxScrollbarOffset - scrollbarY) * (this.scrollPos / (float) this.currentMaxScroll));

        Identifier scrollBarTexture = this.dragScrolling || this.canDragScroll(mouseX, mouseY, scrollbarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, scrollBarTexture, this.guiLeft + 156, this.guiTop + scrollbarY, 6, 27);
    }

    protected boolean cannotScroll() {
        return this.origin == null || this.currentMaxScroll <= 0;
    }

    protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
        return (mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 156 + 6) && (mouseY >= this.guiTop + scrollBarY && mouseY < this.guiTop + scrollBarY + 27);
    }

    protected void renderBadgeTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int widthLimit = this.width - mouseX - 24;
        if (this.isWithinWindowBoundaries(mouseX, mouseY))
            this.renderedBadges.stream()
                    .filter(b -> b.isWithinBadgeBoundaries(mouseX, mouseY))
                    .map(b -> b.getTooltipComponents(this.font, widthLimit, delta))
                    .filter(x -> !x.isEmpty())
                    .forEach(t -> graphics.tooltip(this.font, t, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null));
    }

    protected boolean isWithinWindowBoundaries(int mouseX, int mouseY) {
        return (mouseX >= this.guiLeft && mouseX < this.guiLeft + WINDOW_WIDTH) && (mouseY >= this.guiTop && mouseY < this.guiTop + WINDOW_HEIGHT);
    }

    protected void renderOriginWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_BACKGROUND, this.guiLeft, this.guiTop, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (this.origin != null) {
            graphics.enableScissor(this.guiLeft, this.guiTop, this.guiLeft + WINDOW_WIDTH, this.guiTop + WINDOW_HEIGHT);
            this.renderOriginContent(graphics);
            graphics.disableScissor();
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_BORDER, this.guiLeft, this.guiTop, WINDOW_WIDTH, WINDOW_HEIGHT);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_NAME_PLATE, this.guiLeft + 10, this.guiTop + 10, 150, 26);

        if (this.origin != null) {
            graphics.pose().pushMatrix();

            this.renderOriginName(graphics, mouseX, mouseY, delta);
            this.renderOriginImpact(graphics, mouseX, mouseY);

            graphics.pose().popMatrix();
            graphics.centeredText(this.font, this.getTitle(), this.width / 2, this.guiTop - 15, 0xFFFFFFFF);

            this.renderScrollbar(graphics, mouseX, mouseY);
            this.renderBadgeTooltips(graphics, mouseX, mouseY, delta);
        }
    }

    protected void renderOriginImpact(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Impact impact = this.origin.value().impact();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, impact.getSpriteId(), this.guiLeft + 128, this.guiTop + 19, 28, 8);

        if (this.isWithinWindowBoundaries(mouseX, mouseY) && this.isWithinImpactBoundaries(mouseX, mouseY)) {
            MutableComponent impactHoverTooltip = Component.translatable(Origins.MOD_ID + ".gui.impact.impact").append(": ").append(impact.getTextComponent());
            graphics.setTooltipForNextFrame(this.font, impactHoverTooltip, mouseX, mouseY);
        }
    }

    protected boolean isWithinImpactBoundaries(int mouseX, int mouseY) {
        int impactStartX = this.guiLeft + 128;
        int impactStartY = this.guiTop + 19;
        return (mouseX >= impactStartX && mouseX < impactStartX + 28) && (mouseY >= impactStartY && mouseY < impactStartY + 8);
    }

    protected void renderOriginName(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        ItemStack iconStack = this.getCurrentOrigin().value().icon().map(ItemStackReference::create).orElse(ItemStack.EMPTY);
        graphics.item(iconStack, this.guiLeft + 15, this.guiTop + 15);
    }

    protected void renderOriginContent(GuiGraphicsExtractor graphics) {
        assert Minecraft.getInstance().level != null;
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        int textWidthLimit = WINDOW_WIDTH - 48;
        int x = this.guiLeft + 18;
        int y = this.guiTop + 45 - this.scrollPos;

        for (FormattedCharSequence descriptionLine : this.font.split(Origin.getDescription(this.getCurrentOrigin()), textWidthLimit)) {
            graphics.text(this.font, descriptionLine, x + 2, y, 0xFFCCCCCC);
            y += 12;
        }

        y += 12;
        if (this.isOriginRandom) {
            for (FormattedCharSequence randomOriginLine : this.font.split(this.randomOriginText, textWidthLimit)) {
                y += 12;
                graphics.text(this.font, randomOriginLine, x + 2, y, 0xFFCCCCCC);
            }
            y += 14;
        } else {
            for (Holder<Power> holder : RegistryCodecs.listAll(this.origin.value().powers(), access, PowerRegistries.POWER_KEY)) {
                Power power = holder.value();
                if (power.isHidden()) continue;
                LinkedList<FormattedCharSequence> powerName = new LinkedList<>(this.font.split(power.getName(access).withStyle(ChatFormatting.UNDERLINE), textWidthLimit));
                int powerNameWidth = this.font.width(powerName.getLast());

                for (FormattedCharSequence powerNameLine : powerName) {
                    graphics.text(this.font, powerNameLine, x, y, 0xFFFFFFFF);
                    y += 12;
                }
                y -= 12;

                int badgeStartX = x + powerNameWidth + 4, badgeEndX = x + 135, badgeOffsetX = 0, badgeOffsetY = 0;
                ImmutableSet.Builder<Badge> badgeBuilder = ImmutableSet.builder();
                power.collectBadges(badgeBuilder);
                for (Badge badge : badgeBuilder.build()) {
                    int badgeX = badgeStartX + 10 * badgeOffsetX;
                    int badgeY = (y - 1) + 10 * badgeOffsetY;
                    if (badgeX >= badgeEndX) {
                        badgeOffsetX = 0;
                        badgeOffsetY++;

                        badgeX = badgeStartX = x;
                        badgeY = (y - 1) + 10 * badgeOffsetY;
                    }
                    RenderedBadge renderedBadge = new RenderedBadge(power, badge, badgeX, badgeY);
                    this.renderedBadges.add(renderedBadge);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, badge.sprite(), renderedBadge.x, renderedBadge.y, 0.0F, 0.0F, 9, 9, 9, 9);
                    badgeOffsetX++;
                }
                y += badgeOffsetY * 10;
                for (FormattedCharSequence powerDescriptionLine : this.font.split(power.getDescription(access), textWidthLimit)) {
                    y += 12;
                    graphics.text(this.font, powerDescriptionLine, x + 2, y, 0xFFCCCCCC);
                }
                y += 20;
            }
        }

        y += this.scrollPos;
        this.currentMaxScroll = Math.max(0, y - 14 - (this.guiTop + 158));
    }

    protected record RenderedBadge(Power power, Badge badge, int x, int y) {
        public List<ClientTooltipComponent> getTooltipComponents(Font textRenderer, int widthLimit, float delta) {
            return BadgeTooltipManager.getTooltipComponents(this.badge, this.power, textRenderer, widthLimit, delta);
        }

        protected boolean isWithinBadgeBoundaries(int mouseX, int mouseY) {
            return (mouseX >= this.x && mouseX < this.x + 9) && (mouseY >= this.y && mouseY < this.y + 9);
        }
    }
}
