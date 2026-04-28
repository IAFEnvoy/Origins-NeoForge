package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.AttributeEntry;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.helper.AttributePowerHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConditionedAttributePower extends Power implements AttributePowerHelper {
    public static final MapCodec<ConditionedAttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            AttributeEntry.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(ConditionedAttributePower::getModifiers),
            Codec.BOOL.optionalFieldOf("update_health", true).forGetter(ConditionedAttributePower::shouldUpdateHealth)
    ).apply(i, ConditionedAttributePower::new));
    private final List<AttributeEntry> modifiers;
    private final boolean updateHealth;

    public ConditionedAttributePower(BaseSettings settings, List<AttributeEntry> modifiers, boolean updateHealth) {
        super(settings);
        this.modifiers = modifiers;
        this.updateHealth = updateHealth;
    }

    @Override
    public List<AttributeEntry> getModifiers() {
        return this.modifiers;
    }

    @Override
    public boolean shouldUpdateHealth() {
        return this.updateHealth;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void active(@NotNull OriginDataHolder holder) {
        this.modify(holder, true);
    }

    @Override
    public void inactive(@NotNull OriginDataHolder holder) {
        this.modify(holder, false);
    }
}
