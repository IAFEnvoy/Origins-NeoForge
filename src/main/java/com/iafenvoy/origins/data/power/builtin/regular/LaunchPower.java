package com.iafenvoy.origins.data.power.builtin.regular;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.PresetBadges;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LaunchPower extends HasCooldownPower implements Toggleable {
    public static final MapCodec<LaunchPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(LaunchPower::getCooldown),
            Codec.FLOAT.fieldOf("speed").forGetter(LaunchPower::getSpeed),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("sound").forGetter(LaunchPower::getSound),
            KeySettings.CODEC.forGetter(LaunchPower::getKey)
    ).apply(i, LaunchPower::new));
    private final float speed;
    private final Optional<SoundEvent> sound;
    private final KeySettings key;

    public LaunchPower(BaseSettings settings, CooldownSettings cooldown, float speed, Optional<SoundEvent> sound, KeySettings key) {
        super(settings, cooldown);
        this.speed = speed;
        this.sound = sound;
        this.key = key;
    }

    public float getSpeed() {
        return this.speed;
    }

    public Optional<SoundEvent> getSound() {
        return this.sound;
    }

    @Override
    public KeySettings getKey() {
        return this.key;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void collectBadges(ImmutableSet.Builder<Badge> builder) {
        super.collectBadges(builder);
        builder.add(PresetBadges.ACTIVE);
    }

    @Override
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        this.getCooldownComponent(holder).useIfReady(() -> {
            if (this.key.match(key)) {
                Entity entity = holder.getEntity();
                if (entity.level() instanceof ServerLevel serverLevel) {
                    entity.push(0, this.speed, 0);
                    entity.hurtMarked = true;
                    this.sound.ifPresent(s -> serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), s, SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.level().random.nextFloat() * 0.4F + 0.8F)));
                    for (int i = 0; i < 4; ++i)
                        serverLevel.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getRandomY(), entity.getZ(), 8, entity.level().random.nextGaussian(), 0.0D, entity.level().random.nextGaussian(), 0.5);
                }
            }
        });
    }
}
