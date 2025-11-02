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
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<EmptyCondition>> EMPTY = REGISTRY.register("empty", () -> EmptyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<EnchantableCondition>> ENCHANTABLE = REGISTRY.register("enchantable", () -> EnchantableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IsDamageableCondition>> IS_DAMAGEABLE = REGISTRY.register("is_damageable", () -> IsDamageableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<IsEquipableCondition>> IS_EQUIPABLE = REGISTRY.register("is_equipable", () -> IsEquipableCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<MeatCondition>> MEAT = REGISTRY.register("meat", () -> MeatCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<SmeltableCondition>> SMELTABLE = REGISTRY.register("smeltable", () -> SmeltableCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
