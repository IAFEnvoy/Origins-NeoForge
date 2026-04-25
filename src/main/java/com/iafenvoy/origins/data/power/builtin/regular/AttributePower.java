package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
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

//FIXME::Rewrite
@NotImplementedYet
public class AttributePower extends Power {
    public static final MapCodec<AttributePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            AttributeEntry.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(AttributePower::getModifiers)
    ).apply(i, AttributePower::new));
    private final List<AttributeEntry> modifiers;

    public AttributePower(BaseSettings settings, List<AttributeEntry> modifiers) {
        super(settings);
        this.modifiers = modifiers;
    }

    public List<AttributeEntry> getModifiers() {
        return this.modifiers;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    @Override
    public void grant(@NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (AttributeEntry entry : this.modifiers) {
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
    public void revoke(@NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (AttributeEntry entry : this.modifiers) {
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
                return ResourceLocation.parse(this.rawId);
            } catch (Exception e) {
                // Wildcard like *:* - generate a unique ID
                return ResourceLocation.fromNamespaceAndPath("origins", "generated_" + Math.abs(this.rawId.hashCode()));
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
