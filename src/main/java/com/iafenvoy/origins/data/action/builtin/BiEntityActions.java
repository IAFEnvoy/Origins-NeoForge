package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.builtin.bientity.AddToSetAction;
import com.iafenvoy.origins.data.action.builtin.bientity.AddVelocityAction;
import com.iafenvoy.origins.data.action.builtin.bientity.DamageTargetAction;
import com.iafenvoy.origins.data.action.builtin.bientity.RemoveFromSetAction;
import com.iafenvoy.origins.data.action.builtin.bientity.meta.*;
import com.iafenvoy.origins.network.payload.MountPlayerS2CPayload;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.iafenvoy.origins.data.action.SimpleActions.createBiEntity;

@SuppressWarnings("unused")
public final class BiEntityActions {
    public static final DeferredRegister<MapCodec<? extends BiEntityAction>> REGISTRY = DeferredRegister.create(ActionRegistries.BI_ENTITY_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register(Constants.NO_OP_KEY, () -> NoOpAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AddToSetAction>> ADD_TO_SET = REGISTRY.register("add_to_set", () -> AddToSetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AddVelocityAction>> ADD_VELOCITY = REGISTRY.register("add_velocity", () -> AddVelocityAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<DamageTargetAction>> DAMAGE_TARGET = REGISTRY.register("damage_target", () -> DamageTargetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<? extends BiEntityAction>> MOUNT = REGISTRY.register("mount", () -> createBiEntity((source, target) -> {
        if (source.level().isClientSide) return;
        boolean mounted = source.startRiding(target);
        if (target instanceof ServerPlayer)
            PacketDistributor.sendToAllPlayers(new MountPlayerS2CPayload(source.getId(), target.getId()));
    }));
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<RemoveFromSetAction>> REMOVE_FROM_SET = REGISTRY.register("remove_from_set", () -> RemoveFromSetAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<? extends BiEntityAction>> SET_IN_LOVE = REGISTRY.register("set_in_love", () -> createBiEntity((Entity source, Entity target) -> {
        if (target instanceof Animal animal) animal.setInLove(source instanceof Player player ? player : null);
    }));
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<? extends BiEntityAction>> TAME = REGISTRY.register("tame", () -> createBiEntity((source, target) -> {
        if (source instanceof Player player && target instanceof TamableAnimal ownable)
            ownable.tame(player);
    }));
    //Meta
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<InvertAction>> INVERT = REGISTRY.register("invert", () -> InvertAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<SourceActionAction>> SOURCE_ACTION = REGISTRY.register("source_action", () -> SourceActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends BiEntityAction>, MapCodec<TargetActionAction>> TARGET_ACTION = REGISTRY.register("target_action", () -> TargetActionAction.CODEC);
}
