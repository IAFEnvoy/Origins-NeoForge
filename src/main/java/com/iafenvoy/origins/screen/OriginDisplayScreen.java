package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.BadgeManager;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.TextAlignment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class OriginDisplayScreen extends Screen {
    private static final ResourceLocation WINDOW_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/background");
    private static final ResourceLocation WINDOW_BORDER = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/border");
    private static final ResourceLocation WINDOW_NAME_PLATE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/name_plate");
    private static final ResourceLocation WINDOW_SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar");
    private static final ResourceLocation WINDOW_SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar/pressed");
    private static final ResourceLocation WINDOW_SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "choose_origin/scroll_bar/slot");

    protected static final int WINDOW_WIDTH = 176;
    protected static final int WINDOW_HEIGHT = 182;

    private final LinkedList<RenderedBadge> renderedBadges = new LinkedList<>();

    protected final boolean showDirtBackground;

    private Holder<Origin> origin, prevOrigin;
    private Holder<Layer> layer, prevLayer;
    private Component randomOriginText;
    private ScrollingTextWidget originNameWidget;

    private boolean refreshOriginNameWidget = false;

    private boolean isOriginRandom;
    private boolean dragScrolling = false;

    private double mouseDragStart = 0;

    private int currentMaxScroll = 0;
    private int scrollDragStart = 0;

    protected int guiTop, guiLeft;
    protected int scrollPos = 0;

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
        this.refreshOriginNameWidget = true;

    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.showDirtBackground) {
            super.renderBackground(context, mouseX, mouseY, delta);
        } else {
            this.renderTransparentBackground(context);
        }

    }

    @Override
    public void renderTransparentBackground(GuiGraphics context) {
        context.fillGradient(0, 0, this.width, this.height, -5, 1678774288, -2112876528);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {

        this.renderedBadges.clear();

        super.render(context, mouseX, mouseY, delta);
        this.renderOriginWindow(context, mouseX, mouseY, delta);

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
        if (this.cannotScroll()) {
            return mouseClicked;
        }

        this.dragScrolling = false;

        int scrollBarY = 36;
        int maxScrollBarOffset = 141;

        scrollBarY += (int) ((maxScrollBarOffset - scrollBarY) * (this.scrollPos / (float) this.currentMaxScroll));
        if (!this.canDragScroll(mouseX, mouseY, scrollBarY)) {
            return mouseClicked;
        }

        this.dragScrolling = true;
        this.scrollDragStart = scrollBarY;
        this.mouseDragStart = mouseY;

        return true;

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (!this.dragScrolling) {
            return mouseDragged;
        }

        int delta = (int) (mouseY - this.mouseDragStart);
        int newScrollPos = Math.max(36, Math.min(141, this.scrollDragStart + delta));

        float part = (newScrollPos - 36) / (float) (141 - 36);
        this.scrollPos = (int) (part * this.currentMaxScroll);

        return mouseDragged;

    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {

        int newScrollPos = this.scrollPos - (int) vertical * 4;
        this.scrollPos = Mth.clamp(newScrollPos, 0, this.currentMaxScroll);

        return super.mouseScrolled(x, y, horizontal, vertical);

    }

    public Holder<Origin> getCurrentOrigin() {
        return this.origin;
    }

    public Holder<Layer> getCurrentLayer() {
        return this.layer;
    }

    public ResourceLocation getCurrentOriginId() {
        return this.getCurrentOrigin().unwrapKey().map(ResourceKey::location).orElse(ResourceLocation.withDefaultNamespace(""));
    }

    protected void renderScrollbar(GuiGraphics context, int mouseX, int mouseY) {

        if (this.cannotScroll()) {
            return;
        }

        context.blitSprite(WINDOW_SCROLL_BAR_SLOT, this.guiLeft + 155, this.guiTop + 35, 8, 134);

        int scrollbarY = 36;
        int maxScrollbarOffset = 141;

        scrollbarY += (int) ((maxScrollbarOffset - scrollbarY) * (this.scrollPos / (float) this.currentMaxScroll));

        ResourceLocation scrollBarTexture = this.dragScrolling || this.canDragScroll(mouseX, mouseY, scrollbarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
        context.blitSprite(scrollBarTexture, this.guiLeft + 156, this.guiTop + scrollbarY, 6, 27);

    }

    protected boolean cannotScroll() {
        return this.origin == null || this.currentMaxScroll <= 0;
    }

    protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
        return (mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 156 + 6) && (mouseY >= this.guiTop + scrollBarY && mouseY < this.guiTop + scrollBarY + 27);
    }

    protected void renderBadgeTooltips(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int widthLimit = this.width - mouseX - 24;
        if (this.isWithinWindowBoundaries(mouseX, mouseY)) {
            this.renderedBadges.stream().filter(RenderedBadge::hasTooltip).filter(renderedBadge -> this.isWithinBadgeBoundaries(renderedBadge, mouseX, mouseY)).map(renderedBadge -> renderedBadge.getTooltipComponents(this.font, widthLimit, delta)).forEach(tooltipComponents -> context.renderTooltipInternal(this.font, tooltipComponents, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE));
        }

    }

    protected boolean isWithinWindowBoundaries(int mouseX, int mouseY) {
        return (mouseX >= this.guiLeft && mouseX < this.guiLeft + WINDOW_WIDTH) && (mouseY >= this.guiTop && mouseY < this.guiTop + WINDOW_HEIGHT);
    }

    protected boolean isWithinBadgeBoundaries(RenderedBadge renderedBadge, int mouseX, int mouseY) {
        return (mouseX >= renderedBadge.x && mouseX < renderedBadge.x + 9) && (mouseY >= renderedBadge.y && mouseY < renderedBadge.y + 9);
    }

    protected Component getTitleText() {
        return Component.literal("Origins");
    }

    protected void renderOriginWindow(GuiGraphics context, int mouseX, int mouseY, float delta) {

        context.blitSprite(WINDOW_BACKGROUND, this.guiLeft, this.guiTop, -4, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (this.origin != null) {
            context.enableScissor(this.guiLeft, this.guiTop, this.guiLeft + WINDOW_WIDTH, this.guiTop + WINDOW_HEIGHT);
            this.renderOriginContent(context);
            context.disableScissor();
        }

        context.blitSprite(WINDOW_BORDER, this.guiLeft, this.guiTop, 2, WINDOW_WIDTH, WINDOW_HEIGHT);
        context.blitSprite(WINDOW_NAME_PLATE, this.guiLeft + 10, this.guiTop + 10, 2, 150, 26);

        if (this.origin != null) {

            context.pose().pushPose();
            context.pose().translate(0, 0, 5);

            this.renderOriginName(context, mouseX, mouseY, delta);
            this.renderOriginImpact(context, mouseX, mouseY);

            context.pose().popPose();
            context.drawCenteredString(this.font, this.getTitleText(), this.width / 2, this.guiTop - 15, 0xFFFFFF);

            this.renderScrollbar(context, mouseX, mouseY);
            this.renderBadgeTooltips(context, mouseX, mouseY, delta);

        }

    }

    protected void renderOriginImpact(GuiGraphics context, int mouseX, int mouseY) {
        Impact impact = this.origin.value().impact();
        context.blitSprite(impact.getSpriteId(), this.guiLeft + 128, this.guiTop + 19, 2, 28, 8);

        if (this.isWithinWindowBoundaries(mouseX, mouseY) && this.isWithinImpactBoundaries(mouseX, mouseY)) {
            MutableComponent impactHoverTooltip = Component.translatable(Origins.MOD_ID + ".gui.impact.impact").append(": ").append(impact.getTextComponent());
            context.renderTooltip(this.font, impactHoverTooltip, mouseX, mouseY);
        }

    }

    protected boolean isWithinImpactBoundaries(int mouseX, int mouseY) {

        int impactStartX = this.guiLeft + 128;
        int impactStartY = this.guiTop + 19;

        return (mouseX >= impactStartX && mouseX < impactStartX + 28) && (mouseY >= impactStartY && mouseY < impactStartY + 8);

    }

    protected void renderOriginName(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.refreshOriginNameWidget || (this.origin != this.prevOrigin || this.layer != this.prevLayer)) {
            this.originNameWidget = new ScrollingTextWidget(this.guiLeft + 38, this.guiTop + 18, WINDOW_WIDTH - (62 + 3 * 8), 9, Origin.getName(this.getCurrentOriginId()), true, this.font);
            this.originNameWidget.setAlignment(TextAlignment.LEFT);

            this.refreshOriginNameWidget = false;

            this.prevOrigin = this.origin;
            this.prevLayer = this.layer;

        }

        this.originNameWidget.render(context, mouseX, mouseY, delta);

        ItemStack iconStack = this.getCurrentOrigin().value().icon().orElse(ItemStack.EMPTY);
        context.renderItem(iconStack, this.guiLeft + 15, this.guiTop + 15);

    }

    protected void renderOriginContent(GuiGraphics context) {
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        int textWidthLimit = WINDOW_WIDTH - 48;

        /*
            Without this code, the text may not cover the whole width of the window if the scroll bar isn't shown. However, with this code,
            you'll see 1 frame of misaligned text because the text length (and whether scrolling is enabled) is only evaluated on
            first render :(
         */

//        if (cannotScroll()) {
//            textWidth += 12;
//        }

        int x = this.guiLeft + 18;
        int y = this.guiTop + 45;

        y -= this.scrollPos;

        for (FormattedCharSequence descriptionLine : this.font.split(Origin.getDescription(this.getCurrentOriginId()), textWidthLimit)) {
            context.drawString(this.font, descriptionLine, x + 2, y, 0xCCCCCC);
            y += 12;
        }

        y += 12;
        if (this.isOriginRandom) {

            for (FormattedCharSequence randomOriginLine : this.font.split(this.randomOriginText, textWidthLimit)) {
                y += 12;
                context.drawString(this.font, randomOriginLine, x + 2, y, 0xCCCCCC);
            }

            y += 14;

        } else {

            for (Holder<Power> power : this.origin.value().powers()) {

                if (power.value().hidden()) {
                    continue;
                }

                LinkedList<FormattedCharSequence> powerName = new LinkedList<>(this.font.split(power.value().getName(access).withStyle(ChatFormatting.UNDERLINE), textWidthLimit));
                int powerNameWidth = this.font.width(powerName.getLast());

                for (FormattedCharSequence powerNameLine : powerName) {
                    context.drawString(this.font, powerNameLine, x, y, 0xFFFFFF);
                    y += 12;
                }

                y -= 12;

                int badgeStartX = x + powerNameWidth + 4;
                int badgeEndX = x + 135;

                int badgeOffsetX = 0;
                int badgeOffsetY = 0;

                for (Power selfOrSubPower : this.getSelfOrSubPowers(power.value(), BadgeManager::has)) {
                    for (Badge badge : BadgeManager.get(selfOrSubPower.getId(access))) {

                        int badgeX = badgeStartX + 10 * badgeOffsetX;
                        int badgeY = (y - 1) + 10 * badgeOffsetY;

                        if (badgeX >= badgeEndX) {

                            badgeOffsetX = 0;
                            badgeOffsetY++;

                            badgeX = badgeStartX = x;
                            badgeY = (y - 1) + 10 * badgeOffsetY;

                        }

                        RenderedBadge renderedBadge = new RenderedBadge(selfOrSubPower, badge, badgeX, badgeY);
                        this.renderedBadges.add(renderedBadge);

                        context.blitSprite(badge.spriteId(), renderedBadge.x, renderedBadge.y, -2, 0, 0, 9, 9, 9, 9);
                        badgeOffsetX++;

                    }

                }

                y += badgeOffsetY * 10;

                for (FormattedCharSequence powerDescriptionLine : this.font.split(power.value().getDescription(access), textWidthLimit)) {
                    y += 12;
                    context.drawString(this.font, powerDescriptionLine, x + 2, y, 0xCCCCCC);
                }

                y += 20;

            }

        }

        y += this.scrollPos;
        this.currentMaxScroll = Math.max(0, y - 14 - (this.guiTop + 158));

    }

    protected final Collection<? extends Power> getSelfOrSubPowers(Power power, Predicate<ResourceLocation> selfPredicate) {
        //TODO
//        if (!selfPredicate.test(power) && power instanceof MultiplePower multiplePower) {
//            return multiplePower.getSubPowers();
//        } else {
        return Set.of(power);
//        }
    }

    protected record RenderedBadge(Power power, Badge badge, int x, int y) {
        public List<ClientTooltipComponent> getTooltipComponents(Font textRenderer, int widthLimit, float delta) {
            return this.badge.getTooltipComponents(this.power, textRenderer, widthLimit, delta);
        }

        public boolean hasTooltip() {
            return this.badge.hasTooltip();
        }
    }
}
