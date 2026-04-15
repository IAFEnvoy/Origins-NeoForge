package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class PhasingPower extends Power {
    public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(PhasingPower::isBlacklist),
            Codec.STRING.optionalFieldOf("render_type", "none").forGetter(PhasingPower::getRenderType),
            Codec.FLOAT.optionalFieldOf("view_distance", 10F).forGetter(PhasingPower::getViewDistance),
            BlockCondition.optionalCodec("block_condition").forGetter(PhasingPower::getBlockCondition),
            EntityCondition.optionalCodec("phase_down_condition").forGetter(PhasingPower::getPhaseDownCondition),
            EntityCondition.optionalCodec("condition").forGetter(PhasingPower::getCondition)
    ).apply(i, PhasingPower::new));
    private final boolean blacklist;
    private final String renderType;
    private final float viewDistance;
    private final BlockCondition blockCondition;
    private final EntityCondition phaseDownCondition;
    private final EntityCondition condition;

    public PhasingPower(BaseSettings settings, boolean blacklist, String renderType, float viewDistance, BlockCondition blockCondition, EntityCondition phaseDownCondition, EntityCondition condition) {
        super(settings);
        this.blacklist = blacklist;
        this.renderType = renderType;
        this.viewDistance = viewDistance;
        this.blockCondition = blockCondition;
        this.phaseDownCondition = phaseDownCondition;
        this.condition = condition;
    }

    public boolean isBlacklist() {
        return this.blacklist;
    }

    public String getRenderType() {
        return this.renderType;
    }

    public float getViewDistance() {
        return this.viewDistance;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public EntityCondition getPhaseDownCondition() {
        return this.phaseDownCondition;
    }

    public EntityCondition getCondition() {
        return this.condition;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
