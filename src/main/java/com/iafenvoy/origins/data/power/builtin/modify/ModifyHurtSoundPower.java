package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.WeightedSoundEntry;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class ModifyHurtSoundPower extends Power {
    public static final MapCodec<ModifyHurtSoundPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.fieldOf("muted").forGetter(ModifyHurtSoundPower::isMuted),
            WeightedSoundEntry.LIST_CODEC.fieldOf("sound").forGetter(ModifyHurtSoundPower::getSound),
            Codec.FLOAT.fieldOf("volume").forGetter(ModifyHurtSoundPower::getVolume),
            Codec.FLOAT.fieldOf("pitch").forGetter(ModifyHurtSoundPower::getPitch)
    ).apply(i, ModifyHurtSoundPower::new));
    private final boolean muted;
    private final List<WeightedSoundEntry> sound;
    private final float volume, pitch;

    protected ModifyHurtSoundPower(BaseSettings settings, boolean muted, List<WeightedSoundEntry> sound, float volume, float pitch) {
        super(settings);
        this.muted = muted;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public List<WeightedSoundEntry> getSound() {
        return this.sound;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getVolume() {
        return this.volume;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public Stream<WeightedSoundEntry.SoundHolder> streamSoundHolder() {
        return this.sound.stream().map(s -> new WeightedSoundEntry.SoundHolder(s, this.volume, this.pitch));
    }
}
