package com.iafenvoy.origins.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class OrbOfOriginItem extends Item {
    public OrbOfOriginItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.RARE));
    }
}
