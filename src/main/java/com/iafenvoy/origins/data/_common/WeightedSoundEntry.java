package com.iafenvoy.origins.data._common;

import com.iafenvoy.origins.util.WeightedRandomSelector;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.function.TriConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public record WeightedSoundEntry(List<Holder<SoundEvent>> sounds, int weight) {
    public static final Codec<WeightedSoundEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CombinedCodecs.SOUND.fieldOf("sounds").forGetter(WeightedSoundEntry::sounds),
            Codec.INT.fieldOf("weight").forGetter(WeightedSoundEntry::weight)
    ).apply(instance, WeightedSoundEntry::new));
    public static final Codec<List<WeightedSoundEntry>> LIST_CODEC = CombinedCodecs.combineCodec(CODEC);

    public record SoundHolder(WeightedSoundEntry entry, float volume, float pitch) implements WeightedRandomSelector {
        @Override
        public int weight() {
            return this.entry.weight;
        }

        public void playSound(TriConsumer<SoundEvent, Float, Float> player) {
            this.entry.sounds.forEach(x -> player.accept(x.value(), this.volume, this.pitch));
        }
    }
}
