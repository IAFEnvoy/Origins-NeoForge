package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
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
public record TooltipPower(ItemCondition itemCondition, List<Component> text, int order) implements Power {
    public static final MapCodec<TooltipPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(TooltipPower::itemCondition),
            CombinedCodecs.TEXT.fieldOf("text").forGetter(TooltipPower::text),
            Codec.INT.optionalFieldOf("order", 0).forGetter(TooltipPower::order)
    ).apply(i, TooltipPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void appendTooltips(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null)
            for (TooltipPower power : EntityOriginAttachment.get(player).getPowers(RegularPowers.TOOLTIP, TooltipPower.class))
                if (power.itemCondition.test(player.level(), event.getItemStack()))
                    event.getToolTip().addAll(power.order, power.text);
    }
}
