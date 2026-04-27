package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class TooltipPower extends Power {
    public static final MapCodec<TooltipPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(TooltipPower::getItemCondition),
            CombinedCodecs.TEXT.fieldOf("text").forGetter(TooltipPower::getText),
            Codec.INT.optionalFieldOf("order", 0).forGetter(TooltipPower::getOrder)
    ).apply(i, TooltipPower::new));
    private final ItemCondition itemCondition;
    private final List<Component> text;
    private final int order;

    public TooltipPower(BaseSettings settings, ItemCondition itemCondition, List<Component> text, int order) {
        super(settings);
        this.itemCondition = itemCondition;
        this.text = text;
        this.order = order;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public List<Component> getText() {
        return this.text;
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void appendTooltips(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null)
            for (TooltipPower power : OriginDataHolder.get(player).getPowers(RegularPowers.TOOLTIP, TooltipPower.class))
                if (power.itemCondition.test(player.level(), event.getItemStack()))
                    event.getToolTip().addAll(power.order, power.text);
    }
}
