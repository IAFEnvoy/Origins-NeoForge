package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.common.CooldownSettings;
import com.iafenvoy.origins.data.common.HudRender;
import com.iafenvoy.origins.data.common.KeySettings;
import com.iafenvoy.origins.data.power.HasCooldownPower;
import com.iafenvoy.origins.data.power.HudRenderable;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
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
            ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(FireProjectilePower::getSound),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(FireProjectilePower::getTag),
            KeySettings.CODEC.forGetter(FireProjectilePower::getKey),
            EntityAction.optionalCodec("projectile_action").forGetter(FireProjectilePower::getProjectileAction),
            EntityAction.optionalCodec("shooter_action").forGetter(FireProjectilePower::getShooterAction)
    ).apply(i, FireProjectilePower::new));
    private final EntityType<?> entityType;
    private final int count, interval, startDelay;
    private final float speed, divergence;
    private final Optional<ResourceLocation> sound;
    private final Optional<CompoundTag> tag;
    private final KeySettings key;
    private final EntityAction projectileAction, shooterAction;

    public FireProjectilePower(BaseSettings settings, CooldownSettings cooldown, EntityType<?> entityType, int count, int interval, int startDelay, float speed, float divergence, Optional<ResourceLocation> sound, Optional<CompoundTag> tag, KeySettings key, EntityAction projectileAction, EntityAction shooterAction) {
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

    public Optional<ResourceLocation> getSound() {
        return this.sound;
    }

    public Optional<CompoundTag> getTag() {
        return this.tag;
    }

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
    public void toggle(@NotNull OriginDataHolder holder, String key) {
        //TODO
    }
}
