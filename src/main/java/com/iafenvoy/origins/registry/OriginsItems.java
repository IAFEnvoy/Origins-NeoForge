package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.item.OrbOfOriginItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Origins.MOD_ID);

    public static final DeferredItem<OrbOfOriginItem> ORB_OF_ORIGIN = REGISTRY.register("orb_of_origin", OrbOfOriginItem::new);
}
