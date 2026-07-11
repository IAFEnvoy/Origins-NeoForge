package com.iafenvoy.origins.data._common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;

/**
 * Represents a reference to a Minecraft statistic — either a simple
 * CUSTOM stat (e.g. {@code minecraft:jump}) or a typed stat with a
 * variant value (e.g. {@code minecraft:mined / minecraft:diamond_ore}).
 */
public record StatReference(Either<ResourceLocation, TypedStat> stat) {
    public static final Codec<StatReference> CODEC = Codec.either(ResourceLocation.CODEC, TypedStat.CODEC).xmap(StatReference::new, StatReference::stat);

    /**
     * Resolves this reference into a concrete {@link Stat} object.
     *
     * @return the resolved Stat, or {@code null} if not found
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Stat<?> resolve() {
        return this.stat.map(
                id -> {
                    ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(id);
                    return registered != null ? Stats.CUSTOM.get(registered) : null;
                }, ts -> {
                    StatType statType = BuiltInRegistries.STAT_TYPE.get(ts.statType);
                    if (statType == null) return null;
                    Object value = statType.getRegistry().get(ts.id);
                    if (value == null) return null;
                    return statType.get(value);
                }
        );
    }

    /**
     * A typed stat reference, combining a {@link StatType} identifier
     * and a registry value identifier.
     *
     * @param statType e.g. {@code minecraft:mined}
     * @param id    e.g. {@code minecraft:diamond_ore}
     */
    public record TypedStat(ResourceLocation statType, ResourceLocation id) {
        public static final Codec<TypedStat> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("stat_type").forGetter(TypedStat::statType),
                ResourceLocation.CODEC.fieldOf("id").forGetter(TypedStat::id)
        ).apply(i, TypedStat::new));
    }
}
