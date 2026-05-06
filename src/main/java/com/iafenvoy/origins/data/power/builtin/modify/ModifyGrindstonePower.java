package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.accessor.PowerModifiedGrindstone;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class ModifyGrindstonePower extends Power {
    public static final MapCodec<ModifyGrindstonePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("top_condition").forGetter(ModifyGrindstonePower::getTopItemCondition),
            ItemCondition.optionalCodec("bottom_condition").forGetter(ModifyGrindstonePower::getBottomItemCondition),
            ItemCondition.optionalCodec("output_condition").forGetter(ModifyGrindstonePower::getOutputItemCondition),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyGrindstonePower::getBlockCondition),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(ModifyGrindstonePower::getResultStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyGrindstonePower::getItemAction),
            ItemAction.optionalCodec("item_action_after_grinding").forGetter(ModifyGrindstonePower::getItemActionAfterGrinding),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyGrindstonePower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyGrindstonePower::getBlockAction),
            ResultType.CODEC.optionalFieldOf("result_type", ResultType.UNCHANGED).forGetter(ModifyGrindstonePower::getResultType),
            Modifier.CODEC.optionalFieldOf("xp_modifier").forGetter(ModifyGrindstonePower::getXpModifier)
    ).apply(i, ModifyGrindstonePower::new));

    private final ItemCondition topItemCondition, bottomItemCondition, outputItemCondition;
    private final BlockCondition blockCondition;
    private final Optional<ItemStack> resultStack;
    private final ItemAction itemAction, itemActionAfterGrinding;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final ResultType resultType;
    private final Optional<Modifier> xpModifier;

    public ModifyGrindstonePower(BaseSettings settings, ItemCondition topItemCondition, ItemCondition bottomItemCondition, ItemCondition outputItemCondition, BlockCondition blockCondition, Optional<ItemStack> resultStack, ItemAction itemAction, ItemAction itemActionAfterGrinding, EntityAction entityAction, BlockAction blockAction, ResultType resultType, Optional<Modifier> xpModifier) {
        super(settings);
        this.topItemCondition = topItemCondition;
        this.bottomItemCondition = bottomItemCondition;
        this.outputItemCondition = outputItemCondition;
        this.blockCondition = blockCondition;
        this.resultStack = resultStack;
        this.itemAction = itemAction;
        this.itemActionAfterGrinding = itemActionAfterGrinding;
        this.entityAction = entityAction;
        this.blockAction = blockAction;
        this.resultType = resultType;
        this.xpModifier = xpModifier;
    }

    public ItemCondition getTopItemCondition() {
        return this.topItemCondition;
    }

    public ItemCondition getBottomItemCondition() {
        return this.bottomItemCondition;
    }

    public ItemCondition getOutputItemCondition() {
        return this.outputItemCondition;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public Optional<ItemStack> getResultStack() {
        return this.resultStack;
    }

    public ItemAction getItemAction() {
        return this.itemAction;
    }

    public ItemAction getItemActionAfterGrinding() {
        return this.itemActionAfterGrinding;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }

    public BlockAction getBlockAction() {
        return this.blockAction;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

    public Optional<Modifier> getXpModifier() {
        return this.xpModifier;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public boolean allowsInTop(Entity entity, ItemStack stack) {
        return this.topItemCondition.test(entity.level(), stack);
    }

    public boolean allowsInBottom(Entity entity, ItemStack stack) {
        return this.bottomItemCondition.test(entity.level(), stack);
    }

    public boolean doesApply(Entity entity, ItemStack topStack, ItemStack bottomStack, ItemStack originalOutput, @Nullable BlockPos grindstonePos) {
        Level world = entity.level();
        return this.allowsInTop(entity, topStack)
                && this.allowsInBottom(entity, bottomStack)
                && this.outputItemCondition.test(world, originalOutput)
                && grindstonePos != null && this.blockCondition.test(world, grindstonePos);
    }

    public void setOutput(Entity entity, ItemStack inputTop, ItemStack inputBottom, SlotAccess currentOutputStackReference) {
        switch (this.resultType) {
            case SPECIFIED -> this.resultStack.map(ItemStack::copy).ifPresent(currentOutputStackReference::set);
            case FROM_BOTTOM -> currentOutputStackReference.set(inputBottom.copy());
            case FROM_TOP -> currentOutputStackReference.set(inputTop.copy());
        }
        this.itemAction.execute(entity.level(), entity, currentOutputStackReference);
    }

    public void executeActions(Entity entity, @Nullable BlockPos pos, SlotAccess outputStackRef) {
        this.executeActions(entity, pos);
        this.applyAfterGrindingItemAction(entity, outputStackRef);
    }

    public void executeActions(Entity entity, @Nullable BlockPos pos) {
        this.entityAction.execute(entity);
        if (pos != null) this.blockAction.execute(entity.level(), pos, Direction.UP);
    }

    public void applyAfterGrindingItemAction(Entity entity, SlotAccess outputStackReference) {
        this.itemActionAfterGrinding.execute(entity.level(), entity, outputStackReference);
    }

    public static boolean allowsInTopSlot(GrindstoneMenu screenHandler, ItemStack stack) {
        return screenHandler instanceof PowerModifiedGrindstone powerModifiedGrindstone
                && OriginDataHolder.get(powerModifiedGrindstone.origins$getPlayer()).streamActivePowers(ModifyGrindstonePower.class).anyMatch(x -> x.allowsInTop(powerModifiedGrindstone.origins$getPlayer(), stack));
    }

    public static boolean allowsInBottomSlot(GrindstoneMenu screenHandler, ItemStack stack) {
        return screenHandler instanceof PowerModifiedGrindstone powerModifiedGrindstone
                && OriginDataHolder.get(powerModifiedGrindstone.origins$getPlayer()).streamActivePowers(ModifyGrindstonePower.class).anyMatch(x -> x.allowsInBottom(powerModifiedGrindstone.origins$getPlayer(), stack));
    }

    public enum ResultType implements StringRepresentable {
        UNCHANGED, SPECIFIED, FROM_TOP, FROM_BOTTOM;
        public static final Codec<ResultType> CODEC = StringRepresentable.fromValues(ResultType::values);

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
