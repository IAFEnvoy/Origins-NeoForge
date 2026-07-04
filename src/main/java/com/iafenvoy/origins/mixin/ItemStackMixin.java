package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import com.iafenvoy.origins.data.power.builtin.regular.EdibleItemPower;
import com.iafenvoy.origins.data.power.builtin.regular.ItemOnItemPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements EntityLinkedItemStack {

    @Shadow
    @Nullable
    public abstract Entity getEntityRepresentation();

    @Unique
    @Nullable
    private WeakReference<Entity> origins$holdingEntity;

    // ===== EntityLinkedItemStack implementation =====

    @Override
    public Entity origins$getEntity() {
        return origins$getEntity(true);
    }

    @Override
    public Entity origins$getEntity(boolean prioritiseVanillaHolder) {
        Entity vanillaHolder = getEntityRepresentation();
        if (prioritiseVanillaHolder && vanillaHolder != null) {
            return vanillaHolder;
        }
        if (origins$holdingEntity != null) {
            return origins$holdingEntity.get();
        }
        return null;
    }

    @Override
    public void origins$setEntity(Entity entity) {
        this.origins$holdingEntity = new WeakReference<>(entity);
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private ItemStack origins$moveEntityToCopy(ItemStack copy) {
        if (origins$holdingEntity != null) {
            ((EntityLinkedItemStack) (Object) copy).origins$setEntity(origins$holdingEntity.get());
        }
        return copy;
    }

    // ===== ItemOnItem =====

    @Inject(method = "overrideOtherStackedOnMe", at = @At("RETURN"), cancellable = true)
    public void onItemOnItem(ItemStack other, Slot slot, ClickAction action, Player pPlayer, SlotAccess otherAccess, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (ItemOnItemPower.execute(pPlayer, slot, otherAccess, action))
            cir.setReturnValue(true);
    }

    // ===== EdibleItem - use =====

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private InteractionResultHolder<ItemStack> origins$onItemUse(Item item, Level world, Player user, InteractionHand hand, Operation<InteractionResultHolder<ItemStack>> original) {
        ItemStack useStack = (ItemStack) (Object) this;
        boolean canConsumeCustomFood = EdibleItemPower.get(useStack, user)
                .map(EdibleItemPower::getFoodProperties)
                .map(fc -> user.canEat(fc.canAlwaysEat()))
                .orElse(false);

        if (canConsumeCustomFood) {
            return ItemUtils.startUsingInstantly(world, user, hand);
        }
        return original.call(item, world, user, hand);
    }

    // ===== EdibleItem - usageTick =====

    @WrapOperation(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"))
    private void origins$onUsageTick(Item item, Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {
        ItemStack usingStack = (ItemStack) (Object) this;
        if (EdibleItemPower.get(usingStack, user).isEmpty()) {
            original.call(item, world, user, usingStack, remainingUseTicks);
        }
    }

    // ===== EdibleItem - onStoppedUsing =====

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"))
    private void origins$onStoppedUsing(Item item, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, Operation<Void> original) {
        if (EdibleItemPower.get(stack, user).isEmpty()) {
            original.call(item, stack, world, user, remainingUseTicks);
        }
    }

    // ===== EdibleItem - finishUsing =====

    @WrapOperation(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack origins$onFinishUsing(Item item, ItemStack stack, Level world, LivingEntity user, Operation<ItemStack> original) {
        return EdibleItemPower.get(stack, user)
                .map(p -> user.eat(world, stack, p.getFoodProperties()))
                .orElseGet(() -> original.call(item, stack, world, user));
    }

    // ===== EdibleItem - getUseAction =====

    @ModifyReturnValue(method = "getUseAnimation", at = @At("RETURN"))
    private UseAnim origins$replaceUseAnimation(UseAnim original) {
        return EdibleItemPower.get((ItemStack) (Object) this, null)
                .map(EdibleItemPower::getConsumeAnimation)
                .orElse(original);
    }

    // ===== EdibleItem - getEatingSound =====

    @ModifyReturnValue(method = "getEatingSound", at = @At("RETURN"))
    private SoundEvent origins$replaceEatingSound(SoundEvent original) {
        return EdibleItemPower.get((ItemStack) (Object) this, null)
                .map(EdibleItemPower::getConsumeSound)
                .orElse(original);
    }

    // ===== EdibleItem - getDrinkingSound =====

    @ModifyReturnValue(method = "getDrinkingSound", at = @At("RETURN"))
    private SoundEvent origins$replaceDrinkingSound(SoundEvent original) {
        return EdibleItemPower.get((ItemStack) (Object) this, null)
                .map(EdibleItemPower::getConsumeSound)
                .orElse(original);
    }

    // ===== EdibleItem - getUseDuration =====

    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private int origins$modifyUseDuration(int original, LivingEntity user) {
        return EdibleItemPower.get((ItemStack) (Object) this, user)
                .map(p -> original)
                .orElse(original);
    }

    // ===== EdibleItem - isUsedOnRelease =====

    @WrapOperation(method = "useOnRelease", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOnRelease(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean origins$useOnReleaseIfCustomFood(Item item, ItemStack stack, Operation<Boolean> original) {
        return EdibleItemPower.get(stack, null).isEmpty()
                ? original.call(item, stack)
                : false;
    }
}
