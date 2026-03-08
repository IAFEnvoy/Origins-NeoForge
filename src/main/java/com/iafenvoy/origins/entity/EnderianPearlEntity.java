package com.iafenvoy.origins.entity;

import com.iafenvoy.origins.registry.OriginsEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EnderianPearlEntity extends ThrowableItemProjectile {

    public EnderianPearlEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public EnderianPearlEntity(Level level, LivingEntity owner) {
        super(OriginsEntities.ENDERIAN_PEARL.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        Entity owner = this.getOwner();

        for (int i = 0; i < 32; ++i) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.level().isClientSide && !this.isRemoved()) {
            if (owner instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.connection.isAcceptingMessages() && serverPlayer.level() == this.level() && !serverPlayer.isSleeping()) {
                    if (owner.isPassenger()) {
                        owner.stopRiding();
                    }
                    owner.teleportTo(this.getX(), this.getY(), this.getZ());
                    owner.fallDistance = 0.0F;
                }
            } else if (owner != null) {
                owner.teleportTo(this.getX(), this.getY(), this.getZ());
                owner.fallDistance = 0.0F;
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        if (owner instanceof Player && !owner.isAlive()) {
            this.discard();
        } else {
            super.tick();
        }
    }
}
