package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.modify.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ModifyPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyAttributePower>> MODIFY_ATTRIBUTE = REGISTRY.register("modify_attribute", () -> ModifyAttributePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyBlockRenderPower>> MODIFY_BLOCK_RENDER = REGISTRY.register("modify_block_render", () -> ModifyBlockRenderPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyBreakSpeedPower>> MODIFY_BREAK_SPEED = REGISTRY.register("modify_break_speed", () -> ModifyBreakSpeedPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyCraftingPower>> MODIFY_CRAFTING = REGISTRY.register("modify_crafting", () -> ModifyCraftingPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyDamageDealtPower>> MODIFY_DAMAGE_DEALT = REGISTRY.register("modify_damage_dealt", () -> ModifyDamageDealtPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyDamageTakenPower>> MODIFY_DAMAGE_TAKEN = REGISTRY.register("modify_damage_taken", () -> ModifyDamageTakenPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyExhaustionPower>> MODIFY_EXHAUSTION = REGISTRY.register("modify_exhaustion", () -> ModifyExhaustionPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyFallingPower>> MODIFY_FALLING = REGISTRY.register("modify_falling", () -> ModifyFallingPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyFluidRenderPower>> MODIFY_FLUID_RENDER = REGISTRY.register("modify_fluid_render", () -> ModifyFluidRenderPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyFogTypePower>> MODIFY_FOG_TYPE = REGISTRY.register("modify_fog_type", () -> ModifyFogTypePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyFoodPower>> MODIFY_FOOD = REGISTRY.register("modify_food", () -> ModifyFoodPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyGrindstonePower>> MODIFY_GRINDSTONE = REGISTRY.register("modify_grindstone", () -> ModifyGrindstonePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyHarvestPower>> MODIFY_HARVEST = REGISTRY.register("modify_harvest", () -> ModifyHarvestPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyJumpPower>> MODIFY_JUMP = REGISTRY.register("modify_jump", () -> ModifyJumpPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyPlayerSpawnPower>> MODIFY_PLAYER_SPAWN = REGISTRY.register("modify_player_spawn", () -> ModifyPlayerSpawnPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyStatusEffectPower>> MODIFY_STATUS_EFFECT = REGISTRY.register("modify_status_effect", () -> ModifyStatusEffectPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyTypeTagPower>> MODIFY_TYPE_TAG = REGISTRY.register("modify_type_tag", () -> ModifyTypeTagPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyValueBlockPower>> MODIFY_VALUE_BLOCK = REGISTRY.register("modify_value_block", () -> ModifyValueBlockPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyValuePower>> MODIFY_VALUE = REGISTRY.register("modify_value", () -> ModifyValuePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModifyVelocityPower>> MODIFY_VELOCITY = REGISTRY.register("modify_velocity", () -> ModifyVelocityPower.CODEC);
}
