package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = Origins.MOD_ID)
public final class OriginsLoot {

    private static final ResourceKey<Enchantment> WATER_PROTECTION = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "water_protection"));

    private static final Set<ResourceKey<LootTable>> AFFECTED_TABLES = new HashSet<>();

    private static final ResourceKey<LootTable> SIMPLE_DUNGEON = getAndAddTable("chests/simple_dungeon");
    private static final ResourceKey<LootTable> STRONGHOLD_LIBRARY = getAndAddTable("chests/stronghold_library");
    private static final ResourceKey<LootTable> MINESHAFT = getAndAddTable("chests/abandoned_mineshaft");
    private static final ResourceKey<LootTable> SMALL_UNDERWATER_RUIN = getAndAddTable("chests/underwater_ruin_small");

    private static ResourceKey<LootTable> getAndAddTable(String path) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace(path));
        AFFECTED_TABLES.add(key);
        return key;
    }

    private static SetEnchantmentsFunction.Builder enchantBook(Holder<Enchantment> enchantment, int level) {
        return new SetEnchantmentsFunction.Builder().withEnchantment(enchantment, ConstantValue.exactly(level));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceKey<LootTable> key = event.getKey();
        if (!AFFECTED_TABLES.contains(key)) return;

        Holder<Enchantment> waterProtection = event.getRegistries()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(WATER_PROTECTION);

        if (key.equals(SIMPLE_DUNGEON)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(20)
                            .apply(enchantBook(waterProtection, 1)))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(10)
                            .apply(enchantBook(waterProtection, 2)))
                    .add(EmptyLootItem.emptyItem()
                            .setWeight(80))
                    .build());
        } else if (key.equals(STRONGHOLD_LIBRARY)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(20)
                            .apply(enchantBook(waterProtection, 2)))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(10)
                            .apply(enchantBook(waterProtection, 3)))
                    .add(EmptyLootItem.emptyItem()
                            .setWeight(80))
                    .build());
        } else if (key.equals(MINESHAFT)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(20)
                            .apply(enchantBook(waterProtection, 1)))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(5)
                            .apply(enchantBook(waterProtection, 2)))
                    .add(EmptyLootItem.emptyItem()
                            .setWeight(90))
                    .build());
        } else if (key.equals(SMALL_UNDERWATER_RUIN)) {
            event.getTable().addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(10)
                            .apply(enchantBook(waterProtection, 1)))
                    .add(LootItem.lootTableItem(Items.BOOK)
                            .setWeight(20)
                            .apply(enchantBook(waterProtection, 2)))
                    .add(EmptyLootItem.emptyItem()
                            .setWeight(110))
                    .build());
        }
    }
}
