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
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemDamageAction>> DAMAGE = REGISTRY.register("damage", () -> ItemDamageAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<HolderActionAction>> HOLDER_ACTION = REGISTRY.register("holder_action", () -> HolderActionAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<MergeComponentAction>> MERGE_COMPONENT = REGISTRY.register("merge_component", () -> MergeComponentAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<RemoveEnchantmentAction>> REMOVE_ENCHANTMENT = REGISTRY.register("remove_enchantment", () -> RemoveEnchantmentAction.CODEC);
    //Meta
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemAndAction>> AND = REGISTRY.register("and", () -> ItemAndAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemChanceAction>> CHANCE = REGISTRY.register("chance", () -> ItemChanceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemChoiceAction>> CHOICE = REGISTRY.register("choice", () -> ItemChoiceAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemDelayAction>> DELAY = REGISTRY.register("delay", () -> ItemDelayAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemIfElseAction>> IF_ELSE = REGISTRY.register("if_else", () -> ItemIfElseAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemIfElseListAction>> IF_ELSE_LIST = REGISTRY.register("if_else_list", () -> ItemIfElseListAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemNothingAction>> NOTHING = REGISTRY.register("nothing", () -> ItemNothingAction.CODEC);
    public static final DeferredHolder<MapCodec<? extends ItemAction>, MapCodec<ItemSideAction>> SIDE = REGISTRY.register("side", () -> ItemSideAction.CODEC);
}
