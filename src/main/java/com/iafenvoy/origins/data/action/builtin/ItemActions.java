package com.iafenvoy.origins.data.action.builtin;

import com.iafenvoy.origins.Constants;
import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.action.ActionRegistries;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.action.NoOpAction;
import com.iafenvoy.origins.data.action.builtin.item.*;
import com.iafenvoy.origins.data.action.builtin.item.meta.*;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ItemActions {
    public static final DeferredRegister<MapCodec<? extends ItemAction>> REGISTRY = DeferredRegister.create(ActionRegistries.ITEM_ACTION, Origins.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<NoOpAction>> NO_OP = REGISTRY.register(Constants.NO_OP_KEY, () -> NoOpAction.CODEC);
    //List
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ConsumeAction>> CONSUME = REGISTRY.register("consume", () -> ConsumeAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<DamageAction>> DAMAGE = REGISTRY.register("damage", () -> DamageAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<HolderActionAction>> HOLDER_ACTION = REGISTRY.register("holder_action", () -> HolderActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<MergeComponentAction>> MERGE_COMPONENT = REGISTRY.register("merge_component", () -> MergeComponentAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<RemoveEnchantmentAction>> REMOVE_ENCHANTMENT = REGISTRY.register("remove_enchantment", () -> RemoveEnchantmentAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<AndAction>> AND = REGISTRY.register("and", () -> AndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ChanceAction>> CHANCE = REGISTRY.register("chance", () -> ChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<DelayAction>> DELAY = REGISTRY.register("delay", () -> DelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<IfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> IfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<IfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<SideAction>> SIDE = REGISTRY.register("side", () -> SideAction.CODEC);
}
