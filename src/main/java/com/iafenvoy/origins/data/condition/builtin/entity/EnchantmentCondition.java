package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.BiFunction;

public record EnchantmentCondition(Holder<Enchantment> enchantment, Calculation calculation, Comparison comparison,
                                   int compareTo) implements EntityCondition {
    public static final MapCodec<EnchantmentCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentCondition::enchantment),
            Calculation.CODEC.optionalFieldOf("calculation", Calculation.SUM).forGetter(EnchantmentCondition::calculation),
            Comparison.CODEC.fieldOf("comparison").forGetter(EnchantmentCondition::comparison),
            Codec.INT.fieldOf("compare_to").forGetter(EnchantmentCondition::compareTo)
    ).apply(i, EnchantmentCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        int level = 0;
        for (EquipmentSlot slot : EquipmentSlot.values())
            level = this.calculation.process(level, living.getItemBySlot(slot).getEnchantmentLevel(this.enchantment));
        return this.comparison.compare(level, this.compareTo);
    }

    public enum Calculation implements StringRepresentable {
        SUM(Integer::sum),
        MAX(Math::max);
        public static final Codec<Calculation> CODEC = StringRepresentable.fromValues(Calculation::values);
        private final BiFunction<Integer, Integer, Integer> processor;

        Calculation(BiFunction<Integer, Integer, Integer> processor) {
            this.processor = processor;
        }

        public int process(int a, int b) {
            return this.processor.apply(a, b);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
