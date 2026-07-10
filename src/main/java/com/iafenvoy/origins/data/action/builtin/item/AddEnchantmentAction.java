package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;

public record AddEnchantmentAction(Object2IntMap<Holder<Enchantment>> enchantment,
                                   boolean override) implements ItemAction {
    public static final MapCodec<AddEnchantmentAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CollectionCodecs.toIntMap(Enchantment.CODEC).fieldOf("enchantment").forGetter(AddEnchantmentAction::enchantment),
            Codec.BOOL.optionalFieldOf("override", false).forGetter(AddEnchantmentAction::override)
    ).apply(i, AddEnchantmentAction::new));

    @Override
    public @NotNull MapCodec<? extends ItemAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
        ItemStack stack = access.get();
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.getTagEnchantments());
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : this.enchantment.object2IntEntrySet()) {
            Holder<Enchantment> enchantment=entry.getKey();
            int lvl= entry.getIntValue();
            if(mutable.getLevel(enchantment)>=lvl&&!this.override) continue;
            mutable.set(enchantment,lvl);
        }
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
    }
}
