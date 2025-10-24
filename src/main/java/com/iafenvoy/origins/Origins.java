package com.iafenvoy.origins;

import com.iafenvoy.origins.data.action.builtin.BiEntityActionTypes;
import com.iafenvoy.origins.data.action.builtin.BlockActionTypes;
import com.iafenvoy.origins.data.action.builtin.EntityActionTypes;
import com.iafenvoy.origins.data.action.builtin.ItemActionTypes;
import com.iafenvoy.origins.registry.OriginsAttachments;
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

        BiEntityActionTypes.REGISTRY.register(bus);
        BlockActionTypes.REGISTRY.register(bus);
        EntityActionTypes.REGISTRY.register(bus);
        ItemActionTypes.REGISTRY.register(bus);
    }
}
