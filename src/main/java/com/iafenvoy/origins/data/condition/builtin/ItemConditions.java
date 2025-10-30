package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.condition.builtin.item.*;
import com.iafenvoy.origins.data.condition.builtin.item.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ItemConditions {
    public static final DeferredRegister<MapCodec<? extends ItemCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.ITEM_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ComponentCondition>> COMPONENT = REGISTRY.register("component", () -> ComponentCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemEmptyCondition>> EMPTY = REGISTRY.register("empty", () -> ItemEmptyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<EnchantableCondition>> ENCHANTABLE = REGISTRY.register("enchantable", () -> EnchantableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IsDamageableCondition>> IS_DAMAGEABLE = REGISTRY.register("is_damageable", () -> IsDamageableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IsEquipableCondition>> IS_EQUIPABLE = REGISTRY.register("is_equipable", () -> IsEquipableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<MeatCondition>> MEAT = REGISTRY.register("meat", () -> MeatCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<SmeltableCondition>> SMELTABLE = REGISTRY.register("smeltable", () -> SmeltableCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemAndCondition>> AND = REGISTRY.register("and", () -> ItemAndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ItemChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ItemConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemNotCondition>> NOT = REGISTRY.register("not", () -> ItemNotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ItemOrCondition>> OR = REGISTRY.register("or", () -> ItemOrCondition.CODEC);
}
