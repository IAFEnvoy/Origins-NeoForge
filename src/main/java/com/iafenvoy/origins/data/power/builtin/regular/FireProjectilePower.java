package com.iafenvoy.origins.data.power.builtin.regular;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.CooldownSettings;
import com.iafenvoy.origins.data._common.KeySettings;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.badge.PresetBadges;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.util.MiscUtil;
import com.iafenvoy.origins.util.Timeout;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FireProjectilePower extends HasCooldownPower implements Toggleable {
    public static final MapCodec<FireProjectilePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            CooldownSettings.CODEC.forGetter(FireProjectilePower::getCooldown),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(FireProjectilePower::getEntityType),
            Codec.INT.optionalFieldOf("count", 1).forGetter(FireProjectilePower::getCount),
            Codec.INT.optionalFieldOf("interval", 0).forGetter(FireProjectilePower::getInterval),
            Codec.INT.optionalFieldOf("start_delay", 0).forGetter(FireProjectilePower::getStartDelay),
            Codec.FLOAT.optionalFieldOf("speed", 1.5f).forGetter(FireProjectilePower::getSpeed),
            Codec.FLOAT.optionalFieldOf("divergence", 1f).forGetter(FireProjectilePower::getDivergence),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("sound").forGetter(FireProjectilePower::getSound),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(FireProjectilePower::getTag),
            KeySettings.CODEC.forGetter(FireProjectilePower::getKey),
            EntityAction.optionalCodec("projectile_action").forGetter(FireProjectilePower::getProjectileAction),
            EntityAction.optionalCodec("shooter_action").forGetter(FireProjectilePower::getShooterAction)
    ).apply(i, FireProjectilePower::new));
    private final EntityType<?> entityType;
    private final int count, interval, startDelay;
    private final float speed, divergence;
    private final Optional<SoundEvent> sound;
    private final Optional<CompoundTag> tag;
    private final KeySettings key;
    private final EntityAction projectileAction, shooterAction;

    public FireProjectilePower(BaseSettings settings, CooldownSettings cooldown, EntityType<?> entityType, int count, int interval, int startDelay, float speed, float divergence, Optional<SoundEvent> sound, Optional<CompoundTag> tag, KeySettings key, EntityAction projectileAction, EntityAction shooterAction) {
        super(settings, cooldown);
        this.entityType = entityType;
        this.count = count;
        this.interval = interval;
        this.startDelay = startDelay;
        this.speed = speed;
        this.divergence = divergence;
        this.sound = sound;
        this.tag = tag;
        this.key = key;
        this.projectileAction = projectileAction;
        this.shooterAction = shooterAction;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public int getCount() {
        return this.count;
    }

    public int getInterval() {
        return this.interval;
    }

    public int getStartDelay() {
        return this.startDelay;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getDivergence() {
        return this.divergence;
    }

    public Optional<SoundEvent> getSound() {
        return this.sound;
    }

    public Optional<CompoundTag> getTag() {
        return this.tag;
    }

    @Override
    public KeySettings getKey() {
        return this.key;
    }

    public EntityAction getProjectileAction() {
        return this.projectileAction;
    }

    public EntityAction getShooterAction() {
        return this.shooterAction;
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
        Entity entity = holder.getEntity();
        this.getCooldownComponent(holder).useIfReady(() -> {
            if (this.interval <= 0) Timeout.create(this.startDelay, () -> {
                this.playSound(entity);
                for (int i = 0; i < this.count; i++)
                    this.fireProjectile(entity);
            });
            else Timeout.create(this.startDelay - this.interval, () -> Timeout.create(this.interval, this.count, () -> {
                this.playSound(entity);
                this.fireProjectile(entity);
            }));
        });
    }

    public void playSound(Entity player) {
        this.sound.ifPresent(sound -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level().getRandom().nextFloat() * 0.4F + 0.8F)));
    }

    public void fireProjectile(Entity source) {
        if (!(source.level() instanceof ServerLevel level)) return;

        float yaw = source.getYRot();
        float pitch = source.getXRot();

        Optional<Entity> optional = MiscUtil.getEntityWithPassengers(level, this.entityType, this.tag.orElse(new CompoundTag()), source.position().add(0, source.getEyeHeight(source.getPose()), 0), yaw, pitch);
        if (optional.isEmpty()) return;
        Entity entity = optional.get();
        if (entity instanceof Projectile projectile) {
            if (entity instanceof AbstractHurtingProjectile ahp) ahp.accelerationPower = this.speed;
            projectile.setOwner(source);
            projectile.shootFromRotation(source, pitch, yaw, 0F, this.speed, this.divergence);
        } else {
            float f = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
            float g = -Mth.sin(pitch * 0.017453292F);
            float h = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
            RandomSource random = level.getRandom();
            Vec3 vec3d = (new Vec3(f, g, h)).normalize().add(random.nextGaussian() * 0.0075 * (double) this.divergence, random.nextGaussian() * 0.0075 * (double) this.divergence, random.nextGaussian() * 0.0075 * (double) this.divergence).scale(this.speed);
            entity.setDeltaMovement(vec3d);
            Vec3 entityVelo = source.getDeltaMovement();
            entity.setDeltaMovement(entity.getDeltaMovement().add(entityVelo.x, source.onGround() ? 0.0D : entityVelo.y, entityVelo.z));
        }
        this.tag.ifPresent(tag -> {
            CompoundTag mergedTag = entity.saveWithoutId(new CompoundTag());
            mergedTag.merge(tag);
            entity.load(mergedTag);
        });
        level.tryAddFreshEntityWithPassengers(entity);
        this.projectileAction.execute(entity);
        this.shooterAction.execute(source);
    }
}
