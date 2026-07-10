package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.EffectEntry;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackingEffectPower extends Power {
    public static final MapCodec<StackingEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("min_stacks", 0).forGetter(StackingEffectPower::getMinStacks),
            Codec.INT.optionalFieldOf("max_stacks", 10).forGetter(StackingEffectPower::getMaxStacks),
            Codec.INT.optionalFieldOf("duration_per_stack", 10).forGetter(StackingEffectPower::getDurationPerStack),
            EffectEntry.LIST_CODEC.fieldOf("effect").forGetter(StackingEffectPower::getEffects)
    ).apply(i, StackingEffectPower::new));
    private final int minStacks;
    private final int maxStacks;
    private final int durationPerStack;
    private final List<EffectEntry> effects;

    public StackingEffectPower(BaseSettings settings, int minStacks, int maxStacks, int durationPerStack, List<EffectEntry> effects) {
        super(settings);
        this.minStacks = minStacks;
        this.maxStacks = maxStacks;
        this.durationPerStack = durationPerStack;
        this.effects = effects;
    }

    public int getMinStacks() {
        return this.minStacks;
    }

    public int getMaxStacks() {
        return this.maxStacks;
    }

    public int getDurationPerStack() {
        return this.durationPerStack;
    }

    public List<EffectEntry> getEffects() {
        return this.effects;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void activeTick(@NotNull OriginDataHolder holder) {
        super.activeTick(holder);
        if (holder.getEntity() instanceof LivingEntity living)
            for (EffectEntry entry : this.effects)
                living.addEffect(entry.create(Math.max(this.durationPerStack * 2, 20)));
    }
}
