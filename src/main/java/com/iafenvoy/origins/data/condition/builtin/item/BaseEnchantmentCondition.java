package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public record BaseEnchantmentCondition(Holder<Enchantment> enchantment, Comparison comparison) implements ItemCondition {
    public static final MapCodec<BaseEnchantmentCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(BaseEnchantmentCondition::enchantment),
            Comparison.CompareOperation.CODEC.optionalFieldOf("comparison", Comparison.CompareOperation.GREATER_THAN_OR_EQUAL).forGetter(condition -> condition.comparison().comparison()),
            Codec.INT.fieldOf("compare_to").forGetter(condition -> (int) condition.comparison().compareTo())
    ).apply(instance, (enchantment, operation, compareTo) -> new BaseEnchantmentCondition(enchantment, new Comparison(operation, compareTo))));

    @Override
    public @NotNull MapCodec<? extends ItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull net.minecraft.world.level.Level level, @NotNull ItemStack stack) {
        return this.comparison.compare(stack.getTagEnchantments().getLevel(this.enchantment));
    }
}
