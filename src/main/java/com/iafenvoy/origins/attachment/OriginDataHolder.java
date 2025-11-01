package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record OriginDataHolder(Entity entity, EntityOriginAttachment data, EntitySetAttachment entitySet,
                               RegistryAccess access) {
    public Map<Holder<Layer>, Holder<Origin>> getOrigins() {
        return Map.copyOf(this.data.getOrigins());
    }

    public Holder<Origin> getOrigin(Holder<Layer> layer) {
        return this.data.getOrigins().get(layer);
    }

    public void setOrigin(@NotNull Holder<Layer> layer, @NotNull Holder<Origin> origin) {
        this.data.setOrigin(layer, origin, this.entity);
    }

    public void clearOrigin(@NotNull Holder<Layer> layer) {
        this.data.clearOrigin(layer, this.entity);
    }

    @NotNull
    public <T extends Power> Collection<T> getPowers(DeferredHolder<MapCodec<? extends Power>, MapCodec<T>> holder, Class<T> clazz) {
        return this.getPowers(holder.getId(), clazz);
    }

    @NotNull
    public <T extends Power> List<T> getPowers(ResourceLocation id, Class<T> clazz) {
        return this.data().getPowers(id, clazz);
    }

    public boolean hasOrigin(Holder<Layer> layer) {
        return this.data.hasOrigin(layer);
    }

    public void sync() {
        this.data.sync(this.entity);
    }

    public boolean fillAutoChoosing() {
        return this.data.fillAutoChoosing(this.entity);
    }

    public boolean hasAllOrigins() {
        return this.data.hasAllOrigins(this.access);
    }

    public void addEntity(ResourceLocation id, Entity target) {
        this.entitySet.addEntity(this.entity, id, target);
    }

    public void removeEntity(ResourceLocation id, Entity target) {
        this.entitySet.removeEntity(this.entity, id, target);
    }

    public boolean containEntity(ResourceLocation id, Entity target) {
        return this.entitySet().containEntity(id, target);
    }

    public static OriginDataHolder get(Entity entity) {
        return new OriginDataHolder(entity, EntityOriginAttachment.get(entity), EntitySetAttachment.get(entity), entity.registryAccess());
    }
}
