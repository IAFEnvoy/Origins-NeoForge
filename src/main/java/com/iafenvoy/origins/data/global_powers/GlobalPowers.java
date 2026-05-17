package com.iafenvoy.origins.data.global_powers;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public record GlobalPowers(List<Either<EntityType<?>, TagKey<EntityType<?>>>> entityTypes,
                           List<Either<Holder<Power>, TagKey<Power>>> powers) {
    public static final Codec<GlobalPowers> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.either(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), TagKey.hashedCodec(Registries.ENTITY_TYPE)).listOf().fieldOf("entity_types").forGetter(GlobalPowers::entityTypes),
            RegistryCodecs.holderOrTag(PowerRegistries.POWER_KEY).listOf().fieldOf("powers").forGetter(GlobalPowers::powers)
    ).apply(i, GlobalPowers::new));
    public static final Codec<Holder<GlobalPowers>> CODEC = RegistryFixedCodec.create(GlobalPowersRegistries.GLOBAL_POWERS_LEY);
}
