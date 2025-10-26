package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.event.client.ClientGlowingColorEvent;
import com.iafenvoy.origins.event.client.ClientShouldGlowingEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(Dist.CLIENT)
public record SelfGlowPower(EntityCondition entityCondition, BiEntityCondition biEntityCondition, boolean useTeam,
                            int color) implements Power {
    public static final MapCodec<SelfGlowPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.optionalCodec("entity_condition").forGetter(SelfGlowPower::entityCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SelfGlowPower::biEntityCondition),
            Codec.BOOL.optionalFieldOf("use_teams", true).forGetter(SelfGlowPower::useTeam),
            Codec.INT.optionalFieldOf("color", 0xFFFFFFFF).forGetter(SelfGlowPower::color)
    ).apply(i, SelfGlowPower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void handleGlowingColor(ClientGlowingColorEvent event) {
        Player player = Minecraft.getInstance().player;
        Entity entity = event.getEntity();
        if (player != null)
            for (SelfGlowPower power : EntityOriginAttachment.get(entity).getPowers(RegularPowers.SELF_GLOW, SelfGlowPower.class))
                if (!power.useTeam && power.entityCondition.test(player) && power.biEntityCondition.test(entity, player))
                    event.setColor(power.color);
    }

    @SubscribeEvent
    public static void enableGlowing(ClientShouldGlowingEvent event) {
        Player player = Minecraft.getInstance().player;
        Entity entity = event.getEntity();
        if (player != null)
            for (SelfGlowPower power : EntityOriginAttachment.get(entity).getPowers(RegularPowers.SELF_GLOW, SelfGlowPower.class))
                if (!power.useTeam && power.entityCondition.test(player) && power.biEntityCondition.test(entity, player))
                    event.allow();
    }
}
