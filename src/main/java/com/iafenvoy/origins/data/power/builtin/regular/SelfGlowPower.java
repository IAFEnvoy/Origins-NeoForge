package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data._common.helper.GlowPowerHelper;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SelfGlowPower extends Power implements GlowPowerHelper {
    public static final MapCodec<SelfGlowPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            EntityCondition.optionalCodec("entity_condition").forGetter(SelfGlowPower::getEntityCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SelfGlowPower::getBiEntityCondition),
            Codec.BOOL.optionalFieldOf("use_teams", true).forGetter(SelfGlowPower::shouldUseTeam),
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

    @Override
    public EntityCondition getEntityCondition() {
        return this.entityCondition;
    }

    @Override
    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    @Override
    public boolean shouldUseTeam() {
        return this.useTeam;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public boolean canGlow(Player player, Entity entity) {
        return this.entityCondition.test(player) && this.biEntityCondition.test(entity, player);
    }
}
