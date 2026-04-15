package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SelfGlowPower extends Power {
    public static final MapCodec<SelfGlowPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("entity_condition").forGetter(SelfGlowPower::getEntityCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SelfGlowPower::getBiEntityCondition),
            Codec.BOOL.optionalFieldOf("use_teams", true).forGetter(SelfGlowPower::isUseTeam),
            Codec.INT.optionalFieldOf("color", 0xFFFFFFFF).forGetter(SelfGlowPower::getColor)
    ).apply(i, SelfGlowPower::new));
    private final EntityCondition entityCondition;
    private final BiEntityCondition biEntityCondition;
    private final boolean useTeam;
    private final int color;

    public SelfGlowPower(BaseSettings settings, EntityCondition entityCondition, BiEntityCondition biEntityCondition, boolean useTeam, int color) {
        super(settings);
        this.entityCondition = entityCondition;
        this.biEntityCondition = biEntityCondition;
        this.useTeam = useTeam;
        this.color = color;
    }

    public EntityCondition getEntityCondition() {
        return this.entityCondition;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public boolean isUseTeam() {
        return this.useTeam;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @ApiStatus.Internal
    @EventBusSubscriber(Dist.CLIENT)
    public static final class ClientEvents {
        @SubscribeEvent
        public static void handleGlowingColor(ClientGlowingColorEvent event) {
            Player player = Minecraft.getInstance().player;
            Entity entity = event.getEntity();
            if (player != null)
                for (SelfGlowPower power : OriginDataHolder.get(entity).getPowers(RegularPowers.SELF_GLOW, SelfGlowPower.class))
                    if (!power.useTeam && power.entityCondition.test(player) && power.biEntityCondition.test(entity, player))
                        event.setColor(power.color);
        }

        @SubscribeEvent
        public static void enableGlowing(ClientShouldGlowingEvent event) {
            Player player = Minecraft.getInstance().player;
            Entity entity = event.getEntity();
            if (player != null)
                for (SelfGlowPower power : OriginDataHolder.get(entity).getPowers(RegularPowers.SELF_GLOW, SelfGlowPower.class))
                    if (!power.useTeam && power.entityCondition.test(player) && power.biEntityCondition.test(entity, player))
                        event.allow();
        }
    }
}
