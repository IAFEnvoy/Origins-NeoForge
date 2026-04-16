package com.iafenvoy.origins.data.power.builtin;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.power.EmptyPower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.data.power.builtin.regular.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class RegularPowers {
    public static final DeferredRegister<MapCodec<? extends Power>> REGISTRY = DeferredRegister.create(PowerRegistries.POWER_TYPE, Origins.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EmptyPower>> EMPTY = REGISTRY.register("empty", () -> EmptyPower.CODEC);

    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<AttributePower>> ATTRIBUTE = REGISTRY.register("attribute", () -> AttributePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<AttributeModifyTransferPower>> ATTRIBUTE_MODIFY_TRANSFER = REGISTRY.register("attribute_modify_transfer", () -> AttributeModifyTransferPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<BurnPower>> BURN = REGISTRY.register("burn", () -> BurnPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ClimbingPower>> CLIMBING = REGISTRY.register("climbing", () -> ClimbingPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<CooldownPower>> COOLDOWN = REGISTRY.register("cooldown", () -> CooldownPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ConduitPowerOnLandPower>> CONDUIT_POWER_ON_LAND = REGISTRY.register("conduit_power_on_land", () -> ConduitPowerOnLandPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<CreativeFlightPower>> CREATIVE_FLIGHT = REGISTRY.register("creative_flight", () -> CreativeFlightPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DamageOverTimePower>> DAMAGE_OVER_TIME = REGISTRY.register("damage_over_time", () -> DamageOverTimePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<DisableRegenPower>> DISABLE_REGEN = REGISTRY.register("disable_regen", () -> DisableRegenPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EffectImmunityPower>> EFFECT_IMMUNITY = REGISTRY.register("effect_immunity", () -> EffectImmunityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ElytraFlightPower>> ELYTRA_FLIGHT = REGISTRY.register("elytra_flight", () -> ElytraFlightPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EntityGlowPower>> ENTITY_GLOW = REGISTRY.register("entity_glow", () -> EntityGlowPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<EntitySetPower>> ENTITY_SET = REGISTRY.register("entity_set", () -> EntitySetPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ExhaustPower>> EXHAUST = REGISTRY.register("exhaust", () -> ExhaustPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<FireImmunityPower>> FIRE_IMMUNITY = REGISTRY.register("fire_immunity", () -> FireImmunityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<FireProjectilePower>> FIRE_PROJECTILE = REGISTRY.register("fire_projectile", () -> FireProjectilePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<FreezePower>> FREEZE = REGISTRY.register("freeze", () -> FreezePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<IgnoreWaterPower>> IGNORE_WATER = REGISTRY.register("ignore_water", () -> IgnoreWaterPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<InventoryPower>> INVENTORY = REGISTRY.register("inventory", () -> InventoryPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<InvisibilityPower>> INVISIBILITY = REGISTRY.register("invisibility", () -> InvisibilityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<InvulnerabilityPower>> INVULNERABILITY = REGISTRY.register("invulnerability", () -> InvulnerabilityPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<LaunchPower>> LAUNCH = REGISTRY.register("launch", () -> LaunchPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<LikeWaterPower>> LIKE_WATER = REGISTRY.register("like_water", () -> LikeWaterPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ModelColorPower>> MODEL_COLOR = REGISTRY.register("model_color", () -> ModelColorPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<NightVisionPower>> NIGHT_VISION = REGISTRY.register("night_vision", () -> NightVisionPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<OverlayPower>> OVERLAY = REGISTRY.register("overlay", () -> OverlayPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ParticlePower>> PARTICLE = REGISTRY.register("particle", () -> ParticlePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<PhasingPower>> PHASING = REGISTRY.register("phasing", () -> PhasingPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<RecipePower>> RECIPE = REGISTRY.register("recipe", () -> RecipePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ResourcePower>> RESOURCE = REGISTRY.register("resource", () -> ResourcePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<RestrictArmorPower>> RESTRICT_ARMOR = REGISTRY.register("restrict_armor", () -> RestrictArmorPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<ScareCreepersPower>> SCARE_CREEPERS = REGISTRY.register("scare_creepers", () -> ScareCreepersPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<SelfGlowPower>> SELF_GLOW = REGISTRY.register("self_glow", () -> SelfGlowPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<StackingStatusEffectPower>> STACKING_STATUS_EFFECT = REGISTRY.register("stacking_status_effect", () -> StackingStatusEffectPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<StandOnFluidPower>> STAND_ON_FLUID = REGISTRY.register("stand_on_fluid", () -> StandOnFluidPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<TogglePower>> TOGGLE = REGISTRY.register("toggle", () -> TogglePower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<TooltipPower>> TOOLTIP = REGISTRY.register("tooltip", () -> TooltipPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<WaterBreathingPower>> WATER_BREATHING = REGISTRY.register("water_breathing", () -> WaterBreathingPower.CODEC);
    public static final DeferredHolder<MapCodec<? extends Power>, MapCodec<WaterVisionPower>> WATER_VISION = REGISTRY.register("water_vision", () -> WaterVisionPower.CODEC);
}
