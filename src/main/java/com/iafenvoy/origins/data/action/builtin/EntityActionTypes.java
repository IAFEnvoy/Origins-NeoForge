package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.builtin.entity.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class EntityActionTypes {
    public static final DeferredRegister<MapCodec<? extends EntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.ENTITY_ACTION, Origins.MOD_ID);
    //List
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddEffectAction>> ADD_EFFECT = REGISTRY.register("add_effect", () -> AddEffectAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddExperienceAction>> ADD_EXPERIENCE = REGISTRY.register("add_experience", () -> AddExperienceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<BlockActionAction>> BLOCK_ACTION = REGISTRY.register("block_action", () -> BlockActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<CraftingTableAction>> CRAFTING_TABLE = REGISTRY.register("crafting_table", () -> CraftingTableAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DamageAction>> DAMAGE = REGISTRY.register("damage", () -> DamageAction.CODEC);

    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EntityRegionApplyAction>> REGION_APPLY = REGISTRY.register("region_apply", () -> EntityRegionApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RemoveEffectAction>> REMOVE_EFFECT = REGISTRY.register("remove_effect", () -> RemoveEffectAction.CODEC);
}
