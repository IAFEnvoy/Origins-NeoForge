package com.iafenvoy.origins.loot.function;

import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsLootItemFunctions;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddPowerLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<AddPowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
            CombinedCodecs.EQUIPMENT_SLOT_GROUP.optionalFieldOf("slot", List.of(EquipmentSlotGroup.ANY)).forGetter(AddPowerLootFunction::slots),
            Power.CODEC.fieldOf("power").forGetter(AddPowerLootFunction::power),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(AddPowerLootFunction::hidden),
            Codec.BOOL.optionalFieldOf("negative", false).forGetter(AddPowerLootFunction::negative)
    )).apply(instance, AddPowerLootFunction::new));

    private final List<EquipmentSlotGroup> slots;
    private final Holder<Power> power;

    private final boolean hidden;
    private final boolean negative;

    private AddPowerLootFunction(List<LootItemCondition> conditions, List<EquipmentSlotGroup> slots, Holder<Power> power, boolean hidden, boolean negative) {
        super(conditions);
        this.slots = slots;
        this.power = power;
        this.hidden = hidden;
        this.negative = negative;
    }

    public List<EquipmentSlotGroup> slots() {
        return this.slots;
    }

    public Holder<Power> power() {
        return this.power;
    }

    public boolean hidden() {
        return this.hidden;
    }

    public boolean negative() {
        return this.negative;
    }

    @Override
    public @NotNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        stack.set(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.builder()
                .add(stack)
                .add(this.slots, this.power, this.hidden, this.negative)
                .build());
        return stack;
    }
}
