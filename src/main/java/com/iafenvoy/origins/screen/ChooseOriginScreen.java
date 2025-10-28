package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.registry.OriginsItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ChooseOriginScreen extends OriginDisplayScreen {
    private final List<Holder<Layer>> layers;
    private final List<Holder<Origin>> origins;
    private final int currentLayerIndex;
    private Holder<Origin> randomOrigin;
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

    public ChooseOriginScreen(List<Holder<Layer>> layers, int currentLayerIndex, boolean showDirtBackground) {
        super(Component.empty(), showDirtBackground);
        this.layers = layers;
        this.currentLayerIndex = currentLayerIndex;
        this.origins = new ArrayList<>(layers.size());
        this.initRandomOrigin();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Layer currentLayer = this.getCurrentLayer().value();
            currentLayer.collectOrigins(player.registryAccess()).forEach(holder -> {
                if (holder.value().choosable()) {
                    ItemStack iconStack = holder.value().icon().orElse(ItemStack.EMPTY);
                    if (iconStack.is(Items.PLAYER_HEAD) && !iconStack.has(DataComponents.PROFILE))
                        iconStack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
                    this.origins.add(holder);
                }
            });
            this.origins.sort(Comparator.<Holder<Origin>>comparingInt(o -> o.value().impact().getImpactValue()).thenComparingInt(x -> x.value().order()));
            this.maxSelection = currentLayer.getOriginOptionCount(player.registryAccess());
            if (this.maxSelection == 0) {
                this.openNextLayerScreen();
            }

            Holder<Origin> newOrigin = this.getCurrentOrigin();
            this.showOrigin(newOrigin, this.getCurrentLayer(), Objects.equals(newOrigin.getKey(), this.randomOrigin.getKey()));
        }
    }

    private void openNextLayerScreen() {
        Minecraft.getInstance().setScreen(new WaitForNextLayerScreen(this.layers, this.currentLayerIndex, this.showDirtBackground));
    }

    private void initRandomOrigin() {
        this.randomOrigin = Holder.direct(Origin.special(OriginsItems.ORB_OF_ORIGIN.toStack(), Impact.NONE, -1));
        MutableComponent randomOriginText = Component.empty();
        this.layers.get(this.currentLayerIndex).value().collectRandomizableOrigins(Minecraft.getInstance().player.registryAccess()).sorted((ia, ib) -> {
            Origin a = ia.value();
            Origin b = ib.value();
            int impactDelta = Integer.compare(a.impact().getImpactValue(), b.impact().getImpactValue());
            return impactDelta != 0 ? impactDelta : Integer.compare(a.order(), b.order());
        }).forEach(origin -> {
            randomOriginText.append(Origin.getName(origin));
            randomOriginText.append(Component.literal("\n"));
        });
        this.setRandomOriginText(randomOriginText);
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("origins.gui.choose_origin.title", Layer.getName(this.getCurrentLayer()));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.calculatedTop = (this.height - CHOICES_HEIGHT) / 2;
        this.calculatedLeft = (this.width - 405) / 2;
        this.guiTop = (this.height - CHOICES_HEIGHT) / 2;
        this.guiLeft = this.calculatedLeft + CHOICES_WIDTH + 10;
        this.pages = (int) Math.ceil(1.0 * this.maxSelection / COUNT_PER_PAGE);
        int x = 0;
        int y = 0;

        for (int i = 0; i < Math.min(this.maxSelection, COUNT_PER_PAGE); ++i) {
            if (x > 6) {
                x = 0;
                ++y;
            }

            int actualX = 12 + x * 28 + this.calculatedLeft;
            int actualY = 10 + y * 30 + this.calculatedTop;
            //TODO::Rewrite?
            int finalI = i;
            this.addRenderableWidget(Button.builder(Component.empty(), (b) -> {
                int index = finalI + this.currentPage * COUNT_PER_PAGE;
                if (index <= this.maxSelection - 1) {
                    this.currentOriginIndex = index;
                    Holder<Origin> newOrigin = this.getCurrentOrigin();
                    this.showOrigin(newOrigin, this.layers.get(this.currentLayerIndex), newOrigin == this.randomOrigin);
                }
            }).pos(actualX, actualY).size(ORIGIN_ICON_SIZE, ORIGIN_ICON_SIZE).build());
            ++x;
        }
        if (this.maxSelection > COUNT_PER_PAGE) {
            this.addRenderableWidget(Button.builder(Component.literal("<"), (b) -> {
                --this.currentPage;
                if (this.currentPage < 0) this.currentPage = this.pages - 1;
            }).pos(this.calculatedLeft, this.guiTop + CHOICES_HEIGHT + 5).size(20, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal(">"), (b) -> this.currentPage = (this.currentPage + 1) % this.pages).pos(this.calculatedLeft + CHOICES_WIDTH - 20, this.guiTop + CHOICES_HEIGHT + 5).size(20, 20).build());
        }
        if (this.maxSelection > 0) {
            this.addRenderableWidget(Button.builder(Component.translatable("origins.gui.select"), (button) -> {
                if (this.currentOriginIndex == this.origins.size())
                    PacketDistributor.sendToServer(new ChooseOriginC2SPayload(this.getCurrentLayer(), Optional.empty()));
                else
                    PacketDistributor.sendToServer(new ChooseOriginC2SPayload(this.getCurrentLayer(), Optional.of(super.getCurrentOrigin())));
                this.openNextLayerScreen();
            }).bounds(this.guiLeft + 88 - 50, this.guiTop + CHOICES_HEIGHT + 5, 100, 20).build());
        }
    }

    @Override
    public Holder<Layer> getCurrentLayer() {
        return this.layers.get(this.currentLayerIndex);
    }

    @Override
    public Holder<Origin> getCurrentOrigin() {
        if (this.currentOriginIndex == this.origins.size()) return this.randomOrigin;
        else return this.origins.get(this.currentOriginIndex);
    }

    @Override
    public ResourceLocation getCurrentOriginId() {
        return Objects.equals(this.getCurrentOrigin(), this.randomOrigin) ? ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "random") : super.getCurrentOriginId();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.maxSelection == 0) this.openNextLayerScreen();
        else super.render(graphics, mouseX, mouseY, delta);
        this.renderOriginChoicesBox(graphics, mouseX, mouseY, delta);
        this.tickTime += delta;
    }

    public void renderOriginChoicesBox(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.blit(ORIGINS_CHOICES, this.calculatedLeft, this.calculatedTop, 0, 0, CHOICES_WIDTH, CHOICES_HEIGHT);
        int x = 0;
        int y = 0;

        for (int i = this.currentPage * COUNT_PER_PAGE; i < Math.min((this.currentPage + 1) * COUNT_PER_PAGE, this.maxSelection); ++i) {
            if (x > 6) {
                x = 0;
                ++y;
            }

            int actualX = 12 + x * 28 + this.calculatedLeft;
            int actualY = 10 + y * 30 + this.calculatedTop;
            if (i >= this.origins.size()) {
                boolean selected = this.getCurrentOrigin().equals(this.randomOrigin);
                this.renderRandomOrigin(graphics, mouseX, mouseY, delta, actualX, actualY, selected);
            } else {
                Holder<Origin> origin = this.origins.get(i);
                boolean selected = Objects.equals(origin.getKey(), this.getCurrentOrigin().getKey());
                this.renderOriginWidget(graphics, mouseX, mouseY, delta, actualX, actualY, selected, origin);
                graphics.renderItem(origin.value().icon().orElse(ItemStack.EMPTY), actualX + 5, actualY + 5);
            }

            ++x;
        }

        Font var10001 = this.font;
        int var10002 = this.currentPage + 1;
        FormattedCharSequence var13 = Component.literal(var10002 + "/" + this.pages).getVisualOrderText();
        int var10003 = this.calculatedLeft + 109;
        int var10004 = this.guiTop + CHOICES_HEIGHT + 5;
        Objects.requireNonNull(this.font);
        graphics.drawCenteredString(var10001, var13, var10003, var10004 + 9 / 2, 16777215);
    }

    public void renderOriginWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x, int y, boolean selected, Holder<Origin> origin) {
        RenderSystem.setShaderTexture(0, ORIGINS_CHOICES);
        boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + ORIGIN_ICON_SIZE && mouseY < y + ORIGIN_ICON_SIZE;
        GuiEventListener var13 = this.getFocused();
        boolean guiSelected = var13 instanceof Button buttonWidget && buttonWidget.getX() == x && (buttonWidget.getY() == y || mouseHovering);
        int u = (selected ? ORIGIN_ICON_SIZE : 0) + (guiSelected ? 52 : 0);

        graphics.blit(ORIGINS_CHOICES, x, y, 230, u, ORIGIN_ICON_SIZE, ORIGIN_ICON_SIZE);
        Impact impact = origin.value().impact();
        switch (impact.name()) {
            case "NONE" -> graphics.blit(ORIGINS_CHOICES, x, y, 224, guiSelected ? 112 : 104, 8, 8);
            case "LOW" -> graphics.blit(ORIGINS_CHOICES, x, y, 232, guiSelected ? 112 : 104, 8, 8);
            case "MEDIUM" -> graphics.blit(ORIGINS_CHOICES, x, y, 240, guiSelected ? 112 : 104, 8, 8);
            case "HIGH" -> graphics.blit(ORIGINS_CHOICES, x, y, 248, guiSelected ? 112 : 104, 8, 8);
            case "VERY_HIGH" -> graphics.blit(ORIGINS_CHOICES, x, y, 248, guiSelected ? 144 : 136, 8, 8);
            default -> graphics.blit(ORIGINS_CHOICES, x, y, 240, guiSelected ? 144 : 136, 8, 8);
        }

        if (mouseHovering) {
            Component text = Layer.getName(this.getCurrentLayer()).copy().append(": ").append(Origin.getName(origin));
            graphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }

    public void renderRandomOrigin(GuiGraphics graphics, int mouseX, int mouseY, float delta, int x, int y, boolean selected) {
        boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + ORIGIN_ICON_SIZE && mouseY < y + ORIGIN_ICON_SIZE;
        GuiEventListener var12 = this.getFocused();
        boolean guiSelected = var12 instanceof Button buttonWidget && buttonWidget.getX() == x && (buttonWidget.getY() == y || mouseHovering);
        int u = (selected ? ORIGIN_ICON_SIZE : 0) + (guiSelected ? 52 : 0);
        graphics.blit(ORIGINS_CHOICES, x, y, 230, u, ORIGIN_ICON_SIZE, ORIGIN_ICON_SIZE);
        graphics.blit(ORIGINS_CHOICES, x + 6, y + 5, 243, 120, 13, 16);
        int impact = (int) ((double) this.tickTime / (double) 15.0F) % 4;
        graphics.blit(ORIGINS_CHOICES, x, y, 224 + impact * 8, guiSelected ? 112 : 104, 8, 8);
    }
}
