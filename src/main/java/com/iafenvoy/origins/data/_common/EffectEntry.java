package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public record EffectEntry(Holder<MobEffect> effect, int amplifier, boolean showParticles,
                          boolean showIcon) {
    public static final Codec<EffectEntry> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(i -> i.group(
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(EffectEntry::effect),
                    Codec.INT.optionalFieldOf("amplifier", 0).forGetter(EffectEntry::amplifier),
                    Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(EffectEntry::showParticles),
                    Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(EffectEntry::showIcon)
            ).apply(i, EffectEntry::new)),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().xmap(holder -> new EffectEntry(holder, 0, true, true), EffectEntry::effect)
    );
    public static final Codec<List<EffectEntry>> LIST_CODEC = CombinedCodecs.combineCodec(CODEC);

    public MobEffectInstance create(int duration) {
        MobEffectInstance effect = new MobEffectInstance(this.effect, duration, this.amplifier, false, this.showParticles, this.showIcon);
        effect.getCures().clear();
        return effect;
    }
}
