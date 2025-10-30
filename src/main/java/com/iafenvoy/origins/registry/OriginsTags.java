package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OriginsTags {
    public static final TagKey<Item> MEAT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "meat"));
}
