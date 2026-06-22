package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.content.TemporaryCobwebBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Origins.MOD_ID);

    public static final DeferredBlock<TemporaryCobwebBlock> TEMPORARY_COBWEB = REGISTRY.registerBlock("temporary_cobweb", TemporaryCobwebBlock::new);
}
