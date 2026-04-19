package com.iafenvoy.origins;

import com.iafenvoy.jupiter.render.screen.ConfigSelectScreen;
import com.iafenvoy.origins.config.OriginsConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Origins.MOD_ID, dist = Dist.CLIENT)
public final class OriginsClient {
    public OriginsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ConfigSelectScreen.builder(Component.empty(), parent).common(OriginsConfig.INSTANCE).build());
    }
}
