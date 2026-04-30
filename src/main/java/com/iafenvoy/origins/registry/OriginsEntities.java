package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.entity.EnderianPearlEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<EnderianPearlEntity>> ENDERIAN_PEARL = REGISTRY.register("enderian_pearl",
            () -> EntityType.Builder.of(EnderianPearlEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(64)
                    .updateInterval(10)
                    .build("enderian_pearl"));
}
