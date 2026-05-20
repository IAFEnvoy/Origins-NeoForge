package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.PowerHolder;
import com.iafenvoy.origins.event.internal.ElementPostRegisterEvent;
import com.iafenvoy.origins.util.HolderHelper;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.*;

@EventBusSubscriber
public class PowerReference {
    public static final Codec<PowerReference> CODEC = Codec.either(ResourceLocation.CODEC, Codec.STRING).xmap(PowerReference::new, p -> p.id);
    private static final List<PowerReference> REFERENCE_CACHE = new LinkedList<>();
    private Either<ResourceLocation, String> id;

    public PowerReference(Either<ResourceLocation, String> id) {
        this.id = id;
        REFERENCE_CACHE.add(this);
    }

    public Optional<PowerHolder> get(RegistryAccess access) {
        return this.id.left().flatMap(rl -> resolve(rl, access.registryOrThrow(PowerRegistries.POWER_KEY)));
    }

    public static Optional<PowerHolder> resolve(ResourceLocation rl, Registry<Power> registry) {
        if (rl == null) return Optional.empty();

        Set<String> seen = new HashSet<>();
        String ns = rl.getNamespace();
        String path = rl.getPath();

        while (seen.add(path)) {
            MultiplePower mp = null;
            String sub = null;

            for (Split split : generateSplits(path)) {
                Power op = registry.get(ResourceLocation.fromNamespaceAndPath(ns, split.base));
                if (op instanceof MultiplePower multiple) {
                    mp = multiple;
                    sub = split.sub;
                    break;
                }
            }
            if (mp == null) return Optional.empty();
            Power p = mp.getPowers().get(sub);
            if (p == null) return Optional.empty();
            ResourceLocation finalId = ResourceLocation.fromNamespaceAndPath(ns, sub);
            if (!(p instanceof MultiplePower)) return Optional.of(new PowerHolder(finalId, p));
            path = path.substring(0, path.length() - sub.length() - 1);
        }
        return Optional.empty();
    }

    private static List<Split> generateSplits(String path) {
        List<Split> list = new ArrayList<>();
        char[] arr = path.toCharArray();
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == '_') {
                String b = path.substring(0, i);
                String s = path.substring(i + 1);
                list.add(new Split(b, s));
            }
        }
        return list;
    }

    private record Split(String base, String sub) {
    }

    @SubscribeEvent
    public static void processPower1(ElementPostRegisterEvent<Power> event) {
        if (event.getRegistryKey() != PowerRegistries.POWER_KEY) return;
        processPower(event.getRegistry(), event.getElement());
    }

    public static void processPower(Registry<Power> registry, Power p) {
        Holder<Power> parent = registry.wrapAsHolder(p);
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
        REFERENCE_CACHE.clear();
    }

    @SubscribeEvent
    public static void fulfillAllAfterLoad(TagsUpdatedEvent event) {
        Registry<Power> registry = event.getRegistryAccess().registryOrThrow(PowerRegistries.POWER_KEY);
        for (Holder.Reference<Power> p : registry.holders().toList()) {
            if (!(p.value() instanceof MultiplePower power)) continue;
            power.getPowers().values().forEach(x -> x.setParent(Optional.of(p.value())));
        }
    }
}
