package com.iafenvoy.origins.loot.function;

import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsLootItemFunctions;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemovePowerLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<RemovePowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(instance.group(
            CombinedCodecs.EQUIPMENT_SLOT_GROUP.optionalFieldOf("slot", List.of(EquipmentSlotGroup.ANY)).forGetter(RemovePowerLootFunction::slots),
            Power.CODEC.fieldOf("power").forGetter(RemovePowerLootFunction::powerId)
    )).apply(instance, RemovePowerLootFunction::new));

    private final List<EquipmentSlotGroup> slots;
    private final Holder<Power> power;

    private RemovePowerLootFunction(List<LootItemCondition> conditions, List<EquipmentSlotGroup> slots, Holder<Power> power) {
        super(conditions);
        this.slots = slots;
        this.power = power;
    }

    public List<EquipmentSlotGroup> slots() {
        return this.slots;
    }

    public Holder<Power> powerId() {
        return this.power;
    }

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return OriginsLootItemFunctions.REMOVE_POWER.get();
    }

    @Override
    public @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        ItemPowersComponent itemPowers = stack.get(OriginsDataComponents.ITEM_POWERS);
        if (itemPowers == null) return stack;
        ItemPowersComponent newItemPowers = ItemPowersComponent.builder()
                .add(itemPowers)
                .remove(this.slots, this.power, (slot, entry) -> {
                    //TODO
                })
                .build();
        if (newItemPowers.isEmpty()) stack.remove(OriginsDataComponents.ITEM_POWERS.get());
        else stack.set(OriginsDataComponents.ITEM_POWERS.get(), newItemPowers);
        return stack;
    }

//    protected void onSlotsRemoval(LootContext context, EquipmentSlotGroup modifierSlot, ItemPowersComponent.Entry entry) {
//        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
//        Power power = this.power.value();
//        if (entity == null) return;
//
//        PowerHolderComponent powerComponent = PowerHolderComponent.KEY.getNullable(entity);
//        if (powerComponent == null) return;
//
//        Map<ResourceLocation, Collection<Power>> revokedPowers = new HashMap<>();
//
//        for (EquipmentSlot slot : Stream.of(EquipmentSlot.values()).filter(modifierSlot::test).toList()) {
//            ResourceLocation sourceId = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "item/" + slot.getName());
//            if (!revokedPowers.containsKey(sourceId) && modifierSlot.test(slot))
//                revokedPowers.computeIfAbsent(sourceId, k -> new ObjectArrayList<>()).add(power);
//        }
//
//        if (!revokedPowers.isEmpty()) PowerHolderComponent.revokePowers(entity, revokedPowers, true);
//    }
}
