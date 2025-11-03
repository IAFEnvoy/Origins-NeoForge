package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.builtin.entity.*;
import com.iafenvoy.origins.data.action.builtin.entity.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class EntityActions {
    public static final DeferredRegister<MapCodec<? extends EntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.ENTITY_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register(Constants.NO_OP_KEY, () -> NoOpAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ActionOnSetAction>> ACTION_ON_SET = REGISTRY.register("action_on_set", () -> ActionOnSetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddEffectAction>> ADD_EFFECT = REGISTRY.register("add_effect", () -> AddEffectAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AddExperienceAction>> ADD_EXPERIENCE = REGISTRY.register("add_experience", () -> AddExperienceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ApplyEffectAction>> APPLY_EFFECT = REGISTRY.register("apply_effect", () -> ApplyEffectAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<BlockActionAction>> BLOCK_ACTION = REGISTRY.register("block_action", () -> BlockActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<CraftingTableAction>> CRAFTING_TABLE = REGISTRY.register("crafting_table", () -> CraftingTableAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DamageAction>> DAMAGE = REGISTRY.register("damage", () -> DamageAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DismountAction>> DISMOUNT = REGISTRY.register("dismount", () -> DismountAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EmitGameEventAction>> EMIT_GAME_EVENT = REGISTRY.register("emit_game_event", () -> EmitGameEventAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EnderChestAction>> ENDER_CHEST = REGISTRY.register("ender_chest", () -> EnderChestAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<EquippedItemActionAction>> EQUIPPED_ITEM_ACTION = REGISTRY.register("equipped_item_action", () -> EquippedItemActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExecuteCommandAction>> EXECUTE_COMMAND = REGISTRY.register("execute_command", () -> ExecuteCommandAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExhaustAction>> EXHAUST = REGISTRY.register("exhaust", () -> ExhaustAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExplodeAction>> EXPLODE = REGISTRY.register("explode", () -> ExplodeAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ExtinguishAction>> EXTINGUISH = REGISTRY.register("extinguish", () -> ExtinguishAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<FeedAction>> FEED = REGISTRY.register("feed", () -> FeedAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GainAirAction>> GAIN_AIR = REGISTRY.register("gain_air", () -> GainAirAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<GiveItemAction>> GIVE_ITEM = REGISTRY.register("give_item", () -> GiveItemAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<HealAction>> HEAL = REGISTRY.register("heal", () -> HealAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<PlaySoundAction>> PLAY_SOUND = REGISTRY.register("play_sound", () -> PlaySoundAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<PassengerActionAction>> PASSENGER_ACTION = REGISTRY.register("passenger_action", () -> PassengerActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RemoveEffectAction>> REMOVE_EFFECT = REGISTRY.register("remove_effect", () -> RemoveEffectAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RidingActionAction>> RIDING_ACTION = REGISTRY.register("riding_action", () -> RidingActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SetFallDistanceAction>> SET_FALL_DISTANCE = REGISTRY.register("set_fall_distance", () -> SetFallDistanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SetOnFireAction>> SET_ON_FIRE = REGISTRY.register("set_on_fire", () -> SetOnFireAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnEffectCloudAction>> SPAWN_EFFECT_CLOUD = REGISTRY.register("spawn_effect_cloud", () -> SpawnEffectCloudAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnEntityAction>> SPAWN_ENTITY = REGISTRY.register("spawn_entity", () -> SpawnEntityAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SpawnParticlesAction>> SPAWN_PARTICLES = REGISTRY.register("spawn_particles", () -> SpawnParticlesAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SwingHandAction>> SWING_HAND = REGISTRY.register("swing_hand", () -> SwingHandAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<RegionApplyAction>> REGION_APPLY = REGISTRY.register("region_apply", () -> RegionApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SelectorApplyAction>> SELECTOR_APPLY = REGISTRY.register("selector_apply", () -> SelectorApplyAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntityAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
}
