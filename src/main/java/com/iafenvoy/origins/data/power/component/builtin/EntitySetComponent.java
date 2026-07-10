package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.EntitySetPower;
import com.iafenvoy.origins.data.power.component.ComponentHolderProvider;
import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntitySetComponent extends PowerComponent implements ComponentHolderProvider<EntitySetComponent.SetHolder> {
    public static final MapCodec<EntitySetComponent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT).fieldOf("set").forGetter(EntitySetComponent::getSet)
    ).apply(i, EntitySetComponent::new));
    private final Map<UUID, Integer> set;

    public EntitySetComponent() {
        this(Map.of());
    }

    public EntitySetComponent(Map<UUID, Integer> set) {
        this.set = new LinkedHashMap<>(set);
    }

    @Override
    public @NotNull MapCodec<? extends PowerComponent> codec() {
        return CODEC;
    }

    @Override
    public SetHolder constructHolder(OriginDataHolder holder, ResourceLocation id) {
        return new SetHolder(holder, this, id);
    }

    @Override
    public void tick(OriginDataHolder holder, PowerHolder parent) {
        if (!(parent.power() instanceof EntitySetPower power)) return;
        Entity entity = holder.getEntity();
        List<UUID> removal = new LinkedList<>();
        for (Map.Entry<UUID, Integer> e : this.set.entrySet()) {
            int value = e.getValue();
            if (value == 0) {
                removal.add(e.getKey());
                if (entity.level() instanceof ServerLevel serverLevel) {
                    Entity l = serverLevel.getEntity(e.getKey());
                    if (l != null) power.getActionOnRemove().execute(entity, l);
                }
            } else if (value > 0) this.set.computeIfPresent(e.getKey(), (u, i) -> i - 1);
        }
        removal.forEach(this.set::remove);
        if (!removal.isEmpty()) this.markDirty();
    }

    public Map<UUID, Integer> getSet() {
        return this.set;
    }

    public record SetHolder(OriginDataHolder holder, EntitySetComponent component, ResourceLocation id) {
        public void addEntity(Entity target) {
            this.addEntity(target, -1);
        }

        //-1 for unlimited
        public void addEntity(Entity target, int timeLimit) {
            if (!this.component.set.containsKey(target.getUUID())) {
                this.component.set.put(target.getUUID(), timeLimit);
                this.postAdd(target);
            }
        }

        public void removeEntity(Entity target) {
            if (this.component.set.containsKey(target.getUUID())) {
                this.component.set.remove(target.getUUID());
                this.postRemove(target);
            }
        }

        public void removeAllEntities(ServerLevel level) {
            this.component.set.keySet().stream().map(level::getEntity).filter(Objects::nonNull).forEach(this::removeEntity);
        }

        public void postAdd(Entity target) {
            this.holder.streamPowers(this.id, EntitySetPower.class).forEach(x -> x.getActionOnAdd().execute(this.holder.getEntity(), target));
            this.component.markDirty();
        }

        public void postRemove(Entity target) {
            if (target != null)
                this.holder.streamPowers(this.id, EntitySetPower.class).forEach(x -> x.getActionOnRemove().execute(this.holder.getEntity(), target));
            this.component.markDirty();
        }

        public List<UUID> getEntityUuids() {
            return new LinkedList<>(this.component.set.keySet());
        }

        public boolean containEntity(Entity target) {
            return this.component.set.containsKey(target.getUUID());
        }

        public int getSize() {
            return this.component.set.size();
        }

    }
}
