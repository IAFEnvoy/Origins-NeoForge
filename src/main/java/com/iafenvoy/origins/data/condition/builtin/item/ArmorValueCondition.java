package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ArmorValueCondition(Comparison comparison) implements ItemCondition {
    public static final MapCodec<ArmorValueCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.forGetter(ArmorValueCondition::comparison)
    ).apply(i, ArmorValueCondition::new));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
        // 26.1版本：ArmorItem已被移除；护甲值现在来自物品的属性修饰符。
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        double armor = 0;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers())
            if (entry.attribute().equals(Attributes.ARMOR) && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE)
                armor += entry.modifier().amount();
        return this.comparison.compare(armor);
    }
}
