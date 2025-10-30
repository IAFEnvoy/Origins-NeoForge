package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.item.OrbOfOriginItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public final class OriginsItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(Origins.MOD_ID);

    public static final DeferredItem<OrbOfOriginItem> ORB_OF_ORIGIN = REGISTRY.register("orb_of_origin", OrbOfOriginItem::new);

    @SubscribeEvent
    public static void appendToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(ORB_OF_ORIGIN);
    }
}
