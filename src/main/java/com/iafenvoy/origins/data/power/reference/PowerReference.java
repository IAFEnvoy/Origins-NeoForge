package com.iafenvoy.origins.data.power.reference;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Stream;

public class PowerReference {
    public static final Codec<PowerReference> CODEC = WildcardCodec.INSTANCE.xmap(PowerReference::new, p -> p.id);
    private final Identifier id;

    public PowerReference(Identifier id) {
        this.id = id;
    }

    public Optional<PowerHolder> get(HolderLookup.Provider provider) {
        return getHolder(provider, this.id);
    }

    public static Optional<PowerHolder> getHolder(HolderLookup.Provider provider, Identifier id) {
        return listAllPowers(provider).filter(x -> Objects.equals(x.id(), id)).findAny();
    }

    public static Optional<PowerHolder> getHolder(HolderLookup.Provider provider, Power power) {
        return listAllPowers(provider).filter(x -> Objects.equals(x.power(), power)).findAny();
    }

    public static Stream<PowerHolder> listAllPowers(HolderLookup.Provider provider) {
        return provider.lookupOrThrow(PowerRegistries.POWER_KEY).listElements().map(PowerHolder::new).flatMap(PowerHolder::stream);
    }
}
