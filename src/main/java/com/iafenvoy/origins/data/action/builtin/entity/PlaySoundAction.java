package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PlaySoundAction(SoundEvent sound, Optional<SoundSource> category, float volume,
                              float pitch) implements EntityAction {
    public static final MapCodec<PlaySoundAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(PlaySoundAction::sound),
            ExtraEnumCodecs.SOUND_SOURCE.optionalFieldOf("category").forGetter(PlaySoundAction::category),
            Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(PlaySoundAction::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(PlaySoundAction::pitch)
    ).apply(i, PlaySoundAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Entity source) {
        source.level().playSound(null, source.getX(), source.getY(), source.getZ(), this.sound, this.category.orElse(source.getSoundSource()), this.volume, this.pitch);
    }
}
