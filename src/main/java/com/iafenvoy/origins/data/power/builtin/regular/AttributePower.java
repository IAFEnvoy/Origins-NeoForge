package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record AttributePower(List<AttributeEntry> modifiers,
                             EntityCondition condition) implements Power {
    public static final MapCodec<AttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            AttributeEntry.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(AttributePower::modifiers),
            EntityCondition.optionalCodec("condition").forGetter(AttributePower::condition)
    ).apply(i, (modifiers, condition) -> {
        if (modifiers.isEmpty()) return new AttributePower(modifiers, condition);
        return new AttributePower(modifiers, condition);
    }));

    // Also accept a single "modifier" field
    public static final MapCodec<AttributePower> SINGLE_OR_LIST_CODEC = new MapCodec<>() {
        @Override
        public <T> java.util.stream.Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
            return CODEC.keys(ops);
        }

        @Override
        public <T> com.mojang.serialization.DataResult<AttributePower> decode(com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.MapLike<T> input) {
            // Try "modifiers" list first
            var result = CODEC.decode(ops, input);
            if (result.result().isPresent() && !result.result().get().modifiers().isEmpty())
                return result;
            // Try single "modifier"
            T modifierField = input.get("modifier");
            if (modifierField != null) {
                return AttributeEntry.CODEC.parse(ops, modifierField).map(entry -> {
                    EntityCondition cond = EntityCondition.optionalCodec("condition")
                            .decode(ops, input).result().orElse(null);
                    return new AttributePower(List.of(entry), cond != null ? cond : com.iafenvoy.origins.data.condition.AlwaysTrueCondition.INSTANCE);
                });
            }
            return result;
        }

        @Override
        public <T> com.mojang.serialization.RecordBuilder<T> encode(AttributePower input, com.mojang.serialization.DynamicOps<T> ops, com.mojang.serialization.RecordBuilder<T> prefix) {
            return CODEC.encode(input, ops, prefix);
        }
    };

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return SINGLE_OR_LIST_CODEC;
    }

    @Override
    public void grant(Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (AttributeEntry entry : modifiers) {
                entry.attribute().ifPresent(attr -> {
                    AttributeInstance instance = living.getAttribute(attr);
                    if (instance != null && !instance.hasModifier(entry.id())) {
                        instance.addPermanentModifier(new AttributeModifier(entry.id(), entry.amount(), entry.operation()));
                    }
                });
            }
        }
    }

    @Override
    public void revoke(Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (AttributeEntry entry : modifiers) {
                entry.attribute().ifPresent(attr -> {
                    AttributeInstance instance = living.getAttribute(attr);
                    if (instance != null) {
                        instance.removeModifier(entry.id());
                    }
                });
            }
        }
    }

    public record AttributeEntry(String rawId, Optional<Holder<Attribute>> attribute, double amount,
                                 AttributeModifier.Operation operation) {
        public ResourceLocation id() {
            try {
                return ResourceLocation.parse(rawId);
            } catch (Exception e) {
                // Wildcard like *:* - generate a unique ID
                return ResourceLocation.fromNamespaceAndPath("origins", "generated_" + Math.abs(rawId.hashCode()));
            }
        }

        public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("id", "origins:unknown").forGetter(AttributeEntry::rawId),
                BuiltInRegistries.ATTRIBUTE.holderByNameCodec().optionalFieldOf("attribute").forGetter(AttributeEntry::attribute),
                Codec.DOUBLE.optionalFieldOf("amount", 0.0).forGetter(AttributeEntry::amount),
                AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(AttributeEntry::operation)
        ).apply(i, AttributeEntry::new));
    }
}
