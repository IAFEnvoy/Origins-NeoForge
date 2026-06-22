package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StackingEffectPower extends Power {
    public static final MapCodec<StackingEffectPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.INT.optionalFieldOf("min_stacks", 0).forGetter(StackingEffectPower::getMinStacks),
            Codec.INT.optionalFieldOf("max_stacks", 10).forGetter(StackingEffectPower::getMaxStacks),
            Codec.INT.optionalFieldOf("duration_per_stack", 10).forGetter(StackingEffectPower::getDurationPerStack),
            EffectEntry.CODEC.listOf().optionalFieldOf("effect", List.of()).forGetter(StackingEffectPower::getEffects)
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
    public void tick(@NotNull OriginDataHolder holder) {
        super.tick(holder);
        if (!this.isActive(holder)) return;
        if (holder.getEntity() instanceof LivingEntity living) {
            for (EffectEntry entry : this.effects) {
                entry.effect().ifPresent(effect -> {
                    int duration = Math.max(this.durationPerStack * 2, 20);
                    living.addEffect(new MobEffectInstance(effect, duration,
                            entry.amplifier(), entry.ambient(), entry.showParticles(), entry.showIcon()));
                });
            }
        }
    }

    public record EffectEntry(Optional<Holder<MobEffect>> effect, int amplifier, boolean ambient,
                              boolean showParticles, boolean showIcon) {
        public static final Codec<EffectEntry> CODEC = Codec.withAlternative(
                RecordCodecBuilder.create(i -> i.group(
                        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("id").forGetter(EffectEntry::effect),
                        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(EffectEntry::amplifier),
                        Codec.BOOL.optionalFieldOf("ambient", false).forGetter(EffectEntry::ambient),
                        Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(EffectEntry::showParticles),
                        Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(EffectEntry::showIcon)
                ).apply(i, EffectEntry::new)),
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().xmap(
                        holder -> new EffectEntry(Optional.of(holder), 0, false, true, true),
                        entry -> entry.effect().orElseThrow()
                )
        );
    }
}
