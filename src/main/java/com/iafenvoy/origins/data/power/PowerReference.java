package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.event.internal.ElementPostRegisterEvent;
import com.iafenvoy.origins.util.HolderHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.*;

@EventBusSubscriber
public class PowerReference {
    public static final Codec<PowerReference> CODEC = Codec.either(ResourceLocation.CODEC, Codec.STRING).xmap(PowerReference::new, powerReference -> powerReference.id);
    private static final List<PowerReference> UNBOUND_CACHE = new LinkedList<>(), REFERENCE_CACHE = new LinkedList<>();
    private Either<ResourceLocation, String> id;
    private Optional<Holder.Reference<Power>> power = Optional.empty();//parent is for active check only

    public PowerReference(Either<ResourceLocation, String> id) {
        this.id = id;
        REFERENCE_CACHE.add(this);
    }

    public Optional<Holder.Reference<Power>> get() {
        return this.power;
    }

    @SubscribeEvent
    public static void processPower(ElementPostRegisterEvent<Power> event) {
        if (!Objects.equals(event.getRegistryKey(), PowerRegistries.POWER_KEY)) return;
        Power p = event.getElement();
        WritableRegistry<Power> registry = event.getRegistry();
        Holder<Power> parent = registry.wrapAsHolder(p);
        if (p instanceof MultiplePower multiple) {
            ResourceLocation key = event.getKey().location();
            for (Map.Entry<String, Power> entry : multiple.getPowers().entrySet()) {
                Power power = entry.getValue();
                power.setParent(Optional.of(parent.value()));
                ResourceLocation k = key.withSuffix("_" + entry.getKey());
                if (registry.getHolder(k).isEmpty()) Registry.registerForHolder(registry, k, power);
            }
        }
        ResourceLocation pid = HolderHelper.id(parent);
        REFERENCE_CACHE.forEach(r -> {
            Optional<String> s = r.id.right();
            if (s.isPresent()) {
                String[] split = s.get().split(":");
                if (split.length == 2) {
                    ResourceLocation id = ResourceLocation.tryBuild(split[0].replaceAll("\\*", pid.getNamespace()), split[1].replaceAll("\\*", pid.getPath()));
                    if (id != null) r.id = Either.left(id);
                }
            }
        });
        UNBOUND_CACHE.addAll(REFERENCE_CACHE);
        REFERENCE_CACHE.clear();
    }

    @SubscribeEvent
    public static void fulfillAllAfterLoad(TagsUpdatedEvent event) {
        Registry<Power> registry = event.getRegistryAccess().registryOrThrow(PowerRegistries.POWER_KEY);
        for (PowerReference reference : UNBOUND_CACHE)
            reference.id
                    .ifLeft(x -> reference.power = registry.getHolder(x))
                    .ifRight(s -> Origins.LOGGER.error("Cannot parse wildcard {}", s));
        UNBOUND_CACHE.clear();
    }
}
