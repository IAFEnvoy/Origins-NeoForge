package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record RemoveEnchantmentAction(List<Holder<Enchantment>> enchantment, Optional<Integer> level,
                                      boolean resetRepairCost) implements ItemAction {
    public static final MapCodec<RemoveEnchantmentAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CombinedCodecs.ENCHANTMENT.optionalFieldOf("enchantment", List.of()).forGetter(RemoveEnchantmentAction::enchantment),
            Codec.INT.optionalFieldOf("level").forGetter(RemoveEnchantmentAction::level),
            Codec.BOOL.optionalFieldOf("reset_repair_cost", false).forGetter(RemoveEnchantmentAction::resetRepairCost)
    ).apply(i, RemoveEnchantmentAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull ItemStack stack) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.getTagEnchantments());
        for (Holder<Enchantment> enchantment : this.enchantment)
            if (this.level.isEmpty() || mutable.getLevel(enchantment) == this.level.get())
                mutable.set(enchantment, 0);
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        if (this.resetRepairCost) stack.set(DataComponents.REPAIR_COST, 0);
    }
}
