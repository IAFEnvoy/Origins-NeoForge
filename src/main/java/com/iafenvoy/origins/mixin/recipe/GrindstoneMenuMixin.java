package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerModifiedGrindstone;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyGrindstonePower;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu implements PowerModifiedGrindstone {
    @Shadow
    @Final
    Container repairSlots;
    @Shadow
    @Final
    private Container resultSlots;
    @Shadow
    @Final
    public static int INPUT_SLOT;
    @Shadow
    @Final
    public static int ADDITIONAL_SLOT;
    @Shadow
    @Final
    public static int RESULT_SLOT;
    @Shadow
    @Final
    private ContainerLevelAccess access;
    @Unique
    private Player origins$cachedPlayer;
    @Unique
    private List<ModifyGrindstonePower> origins$appliedPowers;

    private GrindstoneMenuMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void cachePlayer(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo ci) {
        this.origins$cachedPlayer = playerInventory.player;
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void modifyResult(CallbackInfo ci) {
        ItemStack topStack = this.repairSlots.getItem(INPUT_SLOT);
        ItemStack bottomStack = this.repairSlots.getItem(ADDITIONAL_SLOT);

        SlotAccess outputStackRef = Mutable.stack(this.resultSlots.getItem(0)).toSlotAccess();
        this.origins$appliedPowers = PowerHelper.get(this.origins$cachedPlayer).streamActive(ModifyGrindstonePower.class)
                .filter(p -> p.doesApply(this.origins$cachedPlayer, topStack, bottomStack, outputStackRef.get(), this.origins$getPos()))
                .peek(p -> p.setOutput(this.origins$cachedPlayer, topStack, bottomStack, outputStackRef))
                .collect(Collectors.toCollection(LinkedList::new));

        this.resultSlots.setItem(0, outputStackRef.get());
        this.broadcastChanges();
    }

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), ordinal = 1)
    private ItemStack performAfterGrindstoneActionsQuickMove(ItemStack original, Player player, int slotIndex, @Local Slot slot) {
        List<ModifyGrindstonePower> applyingPowers = this.origins$getAppliedPowers();
        SlotAccess stackReference = Mutable.stack(original).toSlotAccess();
        if (slotIndex != RESULT_SLOT || applyingPowers == null || applyingPowers.isEmpty()) return original;
        ItemStack copy = original.copy();
        applyingPowers.forEach(mgpt -> mgpt.executeActions(player, this.origins$getPos(), stackReference));
        if (stackReference.get().isEmpty()) this.getSlot(slotIndex).onTake(player, copy);
        return stackReference.get();
    }

    @Override
    public List<ModifyGrindstonePower> origins$getAppliedPowers() {
        return this.origins$appliedPowers;
    }

    @Override
    public Player origins$getPlayer() {
        return this.origins$cachedPlayer;
    }

    @Nullable
    @Override
    public BlockPos origins$getPos() {
        return this.access.evaluate((world, pos) -> pos, BlockPos.ZERO);
    }
}
