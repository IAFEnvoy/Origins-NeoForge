package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NotImplementedYet
public class ModifyEnchantmentLevelPower extends Power implements ModifierPowerHelper {
    public static final MapCodec<ModifyEnchantmentLevelPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(ModifyEnchantmentLevelPower::getEnchantment),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyEnchantmentLevelPower::getItemCondition),
            Modifier.CODEC.listOf().fieldOf("modifier").forGetter(ModifyEnchantmentLevelPower::getModifier)
    ).apply(i, ModifyEnchantmentLevelPower::new));
    private final Holder<Enchantment> enchantment;
    private final ItemCondition itemCondition;
    private final List<Modifier> modifier;

    public ModifyEnchantmentLevelPower(BaseSettings settings, Holder<Enchantment> enchantment, ItemCondition itemCondition, List<Modifier> modifier) {
        super(settings);
        this.enchantment = enchantment;
        this.itemCondition = itemCondition;
        this.modifier = modifier;
    }

    public Holder<Enchantment> getEnchantment() {
        return this.enchantment;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    @Override
    public List<Modifier> getModifier() {
        return this.modifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }
}
