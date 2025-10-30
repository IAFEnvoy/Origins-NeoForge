package com.iafenvoy.origins;

import com.iafenvoy.origins.data.action.builtin.BiEntityActions;
import com.iafenvoy.origins.data.action.builtin.BlockActions;
import com.iafenvoy.origins.data.action.builtin.EntityActions;
import com.iafenvoy.origins.data.action.builtin.ItemActions;
import com.iafenvoy.origins.data.condition.builtin.*;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.registry.OriginsItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Origins.MOD_ID)
public final class Origins {
    public static final String MOD_ID = "origins";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Origins(ModContainer container, IEventBus bus) {
        OriginsAttachments.REGISTRY.register(bus);
        OriginsItems.REGISTRY.register(bus);
        //Action
        BiEntityActions.REGISTRY.register(bus);
        BlockActions.REGISTRY.register(bus);
        EntityActions.REGISTRY.register(bus);
        ItemActions.REGISTRY.register(bus);
        //Condition
        BiEntityConditions.REGISTRY.register(bus);
        BiomeConditions.REGISTRY.register(bus);
        BlockConditions.REGISTRY.register(bus);
        DamageConditions.REGISTRY.register(bus);
        EntityConditions.REGISTRY.register(bus);
        FluidConditions.REGISTRY.register(bus);
        ItemConditions.REGISTRY.register(bus);
        //Powers
        RegularPowers.REGISTRY.register(bus);
    }
}
