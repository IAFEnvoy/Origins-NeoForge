package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.content.TemporaryCobwebBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Origins.MOD_ID);

    public static final DeferredBlock<TemporaryCobwebBlock> TEMPORARY_COBWEB = REGISTRY.register("temporary_cobweb",
            () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOL)
                    .strength(4.0F)
                    .requiresCorrectToolForDrops()
                    .noCollission()));
}
