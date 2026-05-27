package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.PowerHolder;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PowerReference {
    public static final Codec<PowerReference> CODEC = WildcardCodec.INSTANCE.xmap(PowerReference::new, p -> p.id);
    private final ResourceLocation id;

    public PowerReference(ResourceLocation id) {
        this.id = id;
    }

    public Optional<PowerHolder> get(RegistryAccess access) {
        return resolve(this.id, access.registryOrThrow(PowerRegistries.POWER_KEY));
    }

    public static Optional<PowerHolder> resolve(ResourceLocation rl, Registry<Power> registry) {
        if (rl == null) return Optional.empty();

        Power direct = registry.get(rl);
        if (direct != null) return Optional.of(new PowerHolder(rl, direct));

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
            if (!(p instanceof MultiplePower)) return Optional.of(new PowerHolder(rl, p));
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
}
