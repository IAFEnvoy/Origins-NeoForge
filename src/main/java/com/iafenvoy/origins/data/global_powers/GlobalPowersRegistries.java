package com.iafenvoy.origins.data.global_powers;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Collection;
import java.util.stream.Stream;

public final class GlobalPowersRegistries {
    public static final ResourceKey<Registry<GlobalPowers>> GLOBAL_POWERS_LEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Origins.MOD_ID, "global_powers"));

    public static Stream<Holder<Power>> streamPowersForType(RegistryAccess access, EntityType<?> type) {
        return access.lookupOrThrow(GLOBAL_POWERS_LEY).listElements().map(Holder.Reference::value)
                .filter(x -> x.entityTypes().stream().anyMatch(e -> e.map(type::equals, t -> type.builtInRegistryHolder().is(t))))
                .map(GlobalPowers::powers).map(e -> RegistryCodecs.listAll(e, access, PowerRegistries.POWER_KEY)).flatMap(Collection::stream);
    }
}
