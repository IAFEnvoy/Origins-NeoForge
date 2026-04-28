package com.iafenvoy.origins.data._common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public record AttributeEntry(Holder<Attribute> attribute, AttributeModifier.Operation operation, float value,
                             Optional<String> name) {
    public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeEntry::attribute),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEntry::operation),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeEntry::value),
            Codec.STRING.optionalFieldOf("name").forGetter(AttributeEntry::name)
    ).apply(instance, AttributeEntry::new));

    public AttributeModifier buildModifier(ResourceLocation id) {
        return new AttributeModifier(id, this.value, this.operation);
    }
}
