package com.iafenvoy.origins.data.power;

import com.google.common.collect.ImmutableSet;
import com.iafenvoy.origins.Proxies;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.util.codec.AnyMapCodec;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MultiplePower extends Power {
    public static final List<String> KNOWN_KEYS = List.of("type", "name", "description", "condition", "hidden", "loading_priority", "badges");
    public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            AnyMapCodec.create(KNOWN_KEYS, Power.DIRECT_CODEC).forGetter(MultiplePower::getPowers)
    ).apply(i, MultiplePower::new));
    private final Map<String, Power> powers;

    public MultiplePower(BaseSettings settings, Map<String, Power> powers) {
        super(settings);
        this.powers = powers;
    }

    public Map<String, Power> getPowers() {
        return this.powers;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public List<Holder<Power>> getPowers(RegistryAccess access) {
        Registry<Power> registry = access.registryOrThrow(PowerRegistries.POWER_KEY);
        return this.powers.values().stream().map(registry::wrapAsHolder).filter(Holder.Reference.class::isInstance).toList();
    }

    @Override
    public void collectBadges(ImmutableSet.Builder<Badge> builder) {
        super.collectBadges(builder);
        this.powers.values().forEach(p -> p.collectBadges(builder));
    }
}
