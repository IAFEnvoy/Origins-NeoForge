package com.iafenvoy.origins;

import com.iafenvoy.origins.data.action.builtin.BiEntityActions;
import com.iafenvoy.origins.data.action.builtin.BlockActions;
import com.iafenvoy.origins.data.action.builtin.EntityActions;
import com.iafenvoy.origins.data.action.builtin.ItemActions;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.condition.builtin.*;
import com.iafenvoy.origins.data.power.builtin.ActionPowers;
import com.iafenvoy.origins.data.power.builtin.ModifyPowers;
import com.iafenvoy.origins.data.power.builtin.PreventPowers;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.data.power.component.BuiltinComponents;
import com.iafenvoy.origins.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Origins.MOD_ID)
public final class Origins {
    public static final String MOD_ID = "origins";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Origins(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, OriginsConfig.SPEC);
        OriginsAttachments.REGISTRY.register(bus);
        OriginsBlocks.REGISTRY.register(bus);
        OriginsCriterionTriggers.REGISTRY.register(bus);
        OriginsDataComponents.REGISTRY.register(bus);
        OriginsEntities.REGISTRY.register(bus);
        OriginsItems.REGISTRY.register(bus);
        OriginsLootItemConditions.REGISTRY.register(bus);
        OriginsLootItemFunctions.REGISTRY.register(bus);
        OriginsRecipeSerializers.REGISTRY.register(bus);
        //动作
        BiEntityActions.REGISTRY.register(bus);
        BlockActions.REGISTRY.register(bus);
        EntityActions.REGISTRY.register(bus);
        ItemActions.REGISTRY.register(bus);
        //徽章
        BuiltinBadges.REGISTRY.register(bus);
        //条件
        BiEntityConditions.REGISTRY.register(bus);
        BiomeConditions.REGISTRY.register(bus);
        BlockConditions.REGISTRY.register(bus);
        DamageConditions.REGISTRY.register(bus);
        EntityConditions.REGISTRY.register(bus);
        FluidConditions.REGISTRY.register(bus);
        ItemConditions.REGISTRY.register(bus);
        //能力
        ActionPowers.REGISTRY.register(bus);
        ModifyPowers.REGISTRY.register(bus);
        PreventPowers.REGISTRY.register(bus);
        RegularPowers.REGISTRY.register(bus);
        //能力组件
        BuiltinComponents.REGISTRY.register(bus);
    }
}
