package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.content.ChoseOriginCriterionTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsCriterionTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTRY = DeferredRegister.create(Registries.TRIGGER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, ChoseOriginCriterionTrigger> CHOSE_ORIGIN = REGISTRY.register("chose_origin", ChoseOriginCriterionTrigger::new);
}
