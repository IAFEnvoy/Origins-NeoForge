package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.builtin.entity.*;
import com.iafenvoy.origins.data.condition.builtin.entity.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class EntityConditions {
    public static final DeferredRegister<MapCodec<? extends EntityCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.ENTITY_CONDITION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(Constants.ALWAYS_TRUE_KEY, () -> AlwaysTrueCondition.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AbilityCondition>> ABILITY = REGISTRY.register("ability", () -> AbilityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AdvancementCondition>> ADVANCEMENT = REGISTRY.register("advancement", () -> AdvancementCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AirCondition>> AIR = REGISTRY.register("air", () -> AirCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AttributeCondition>> ATTRIBUTE = REGISTRY.register("attribute", () -> AttributeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BiomeInCondition>> BIOME = REGISTRY.register("biome", () -> BiomeInCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BlockInRadiusCondition>> BLOCK_IN_RADIUS = REGISTRY.register("block_in_radius", () -> BlockInRadiusCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<BrightnessCondition>> BRIGHTNESS = REGISTRY.register("brightness", () -> BrightnessCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<CollidedHorizontallyCondition>> COLLIDED_HORIZONTALLY = REGISTRY.register("collided_horizontally", () -> CollidedHorizontallyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<CreativeFlyingCondition>> CREATIVE_FLYING = REGISTRY.register("creative_flying", () -> CreativeFlyingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<DaytimeCondition>> DAYTIME = REGISTRY.register("daytime", () -> DaytimeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<DimensionCondition>> DIMENSION = REGISTRY.register("dimension", () -> DimensionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EntityTypeCondition>> ENTITY_TYPE = REGISTRY.register("entity_type", () -> EntityTypeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<EquippedItemCondition>> EQUIPPED_ITEM = REGISTRY.register("equipped_item", () -> EquippedItemCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ExistsCondition>> EXISTS = REGISTRY.register("exists", () -> ExistsCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ExposedToSkyCondition>> EXPOSED_TO_SKY = REGISTRY.register("exposed_to_sky", () -> ExposedToSkyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<FallFlyingCondition>> FALL_FLYING = REGISTRY.register("fall_flying", () -> FallFlyingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<GamemodeCondition>> GAMEMODE = REGISTRY.register("gamemode", () -> GamemodeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InBlockCondition>> IN_BLOCK = REGISTRY.register("in_block", () -> InBlockCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InRainCondition>> IN_RAIN = REGISTRY.register("in_rain", () -> InRainCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InSnowCondition>> IN_SNOW = REGISTRY.register("in_snow", () -> InSnowCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register("in_tag", () -> InTagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InThunderstormCondition>> IN_THUNDERSTORM = REGISTRY.register("in_thunderstorm", () -> InThunderstormCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<InvisibleCondition>> INVISIBLE = REGISTRY.register("invisible", () -> InvisibleCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<LivingCondition>> LIVING = REGISTRY.register("living", () -> LivingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<MobEffectCondition>> MOB_EFFECT = REGISTRY.register("mob_effect", () -> MobEffectCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<NbtCondition>> NBT = REGISTRY.register("nbt", () -> NbtCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OnBlockCondition>> ON_BLOCK = REGISTRY.register("on_block", () -> OnBlockCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OnFireCondition>> ON_FIRE = REGISTRY.register("on_fire", () -> OnFireCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OriginCondition>> ORIGIN = REGISTRY.register("origin", () -> OriginCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<PowerTypeCondition>> POWER_TYPE = REGISTRY.register("power_type", () -> PowerTypeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SneakingCondition>> SNEAKING = REGISTRY.register("sneaking", () -> SneakingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SprintingCondition>> SPRINTING = REGISTRY.register("sprinting", () -> SprintingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SubmergedInCondition>> SUBMERGED_IN = REGISTRY.register("submerged_in", () -> SubmergedInCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<SwimmingCondition>> SWIMMING = REGISTRY.register("swimming", () -> SwimmingCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<TamedCondition>> TAMED = REGISTRY.register("tamed", () -> TamedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<UsingEffectiveToolCondition>> USING_EFFECTIVE_TOOL = REGISTRY.register("using_effective_tool", () -> UsingEffectiveToolCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<UsingItemCondition>> USING_ITEM = REGISTRY.register("using_item", () -> UsingItemCondition.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register("chance", () -> ChanceCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register("constant", () -> ConstantCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
}
