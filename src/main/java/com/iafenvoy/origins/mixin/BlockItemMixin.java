package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.action.ActionOnBlockPlacePower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventBlockPlacePower;
import com.iafenvoy.origins.data.power.builtin.prevent.PreventItemUsePower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
    private void preventItemUseIfBlockItem(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player != null && PreventItemUsePower.isUsagePrevented(player, context.getItemInHand()))
            cir.setReturnValue(InteractionResult.FAIL);
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private void actionOnBlockPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Nullable @Local Player user, @Local BlockPos toPos, @Local ItemStack stack, @Share("powers") LocalRef<List<ActionOnBlockPlacePower>> powersRef) {
        if (user == null) return;
        Direction direction = context.getClickedFace();
        BlockPos onPos = context.getHitResult().getBlockPos();
        InteractionHand hand = context.getHand();
        List<ActionOnBlockPlacePower> powers = OriginDataHolder.get(user).streamActivePowers(ActionOnBlockPlacePower.class).filter(x -> x.getBlockPlaceSettings().shouldExecute(user, stack, hand, toPos, onPos, direction)).toList();
        powers.forEach(x -> x.getBlockPlaceSettings().executeOtherActions(user, toPos, onPos, direction));
        powersRef.set(powers);
    }

    @Inject(method = "place", at = @At("TAIL"))
    private void actionOnBlockPlacePost(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Share("powers") LocalRef<List<ActionOnBlockPlacePower>> powersRef) {
        Player player = context.getPlayer();
        List<ActionOnBlockPlacePower> powers = powersRef.get();
        if (player != null && powers != null)
            powers.forEach(p -> p.getBlockPlaceSettings().performActorItemStuff(player, context.getHand()));
    }

    @ModifyReturnValue(method = "canPlace", at = @At("RETURN"))
    private boolean preventBlockPlace(boolean original, BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        if (player == null) return original;
        Direction direction = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        InteractionHand hand = context.getHand();
        BlockPos toPos = context.getClickedPos();
        BlockPos onPos = context.getHitResult().getBlockPos();
        List<PreventBlockPlacePower> powers = OriginDataHolder.get(player).streamActivePowers(PreventBlockPlacePower.class).filter(p -> p.getBlockPlaceSettings().shouldExecute(player, stack, hand, toPos, onPos, direction)).toList();
        powers.forEach(x -> {
            x.getBlockPlaceSettings().executeOtherActions(player, toPos, onPos, direction);
            x.getBlockPlaceSettings().performActorItemStuff(player, hand);
        });
        return powers.isEmpty() && original;

    }
}
