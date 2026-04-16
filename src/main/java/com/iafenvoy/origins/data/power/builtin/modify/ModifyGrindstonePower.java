package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.math.Modifier;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
public class ModifyGrindstonePower extends Power {
    public static final MapCodec<ModifyGrindstonePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("top_condition").forGetter(ModifyGrindstonePower::getTopItemCondition),
            ItemCondition.optionalCodec("bottom_condition").forGetter(ModifyGrindstonePower::getBottomItemCondition),
            ItemCondition.optionalCodec("output_condition").forGetter(ModifyGrindstonePower::getOutputItemCondition),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyGrindstonePower::getBlockCondition),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter(ModifyGrindstonePower::getResultStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyGrindstonePower::getResultItemAction),
            ItemAction.optionalCodec("item_action_after_grinding").forGetter(ModifyGrindstonePower::getLateItemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyGrindstonePower::getEntityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyGrindstonePower::getBlockAction),
            ExtraEnumCodecs.enumCodec(ResultType::valueOf).optionalFieldOf("result_type", ResultType.UNCHANGED).forGetter(ModifyGrindstonePower::getResultType),
            Modifier.CODEC.optionalFieldOf("xp_modifier").forGetter(ModifyGrindstonePower::getXpModifier)
    ).apply(i, ModifyGrindstonePower::new));

    private final ItemCondition topItemCondition;
    private final ItemCondition bottomItemCondition;
    private final ItemCondition outputItemCondition;
    private final BlockCondition blockCondition;
    private final Optional<ItemStack> resultStack;
    private final ItemAction resultItemAction;
    private final ItemAction lateItemAction;
    private final EntityAction entityAction;
    private final BlockAction blockAction;
    private final ResultType resultType;
    private final Optional<Modifier> xpModifier;

    public ModifyGrindstonePower(BaseSettings settings, ItemCondition topItemCondition, ItemCondition bottomItemCondition, ItemCondition outputItemCondition, BlockCondition blockCondition, Optional<ItemStack> resultStack, ItemAction resultItemAction, ItemAction lateItemAction, EntityAction entityAction, BlockAction blockAction, ResultType resultType, Optional<Modifier> xpModifier) {
        super(settings);
        this.topItemCondition = topItemCondition;
        this.bottomItemCondition = bottomItemCondition;
        this.outputItemCondition = outputItemCondition;
        this.blockCondition = blockCondition;
        this.resultStack = resultStack;
        this.resultItemAction = resultItemAction;
        this.lateItemAction = lateItemAction;
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

    public ItemAction getResultItemAction() {
        return this.resultItemAction;
    }

    public ItemAction getLateItemAction() {
        return this.lateItemAction;
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

    public void tryExecute(ModifyGrindstonePower power, Entity entity, ItemStack itemStack, Optional<BlockPos> pos) {
        power.getLateItemAction().execute(entity.level(), entity, itemStack);
        power.getEntityAction().execute(entity);
        pos.ifPresent(blockPos -> power.getBlockAction().execute(entity.level(), blockPos, Direction.UP));
    }

    public boolean doesApply(ModifyGrindstonePower power, Level level, ItemStack top, ItemStack bottom, ItemStack original, Optional<BlockPos> pos) {
        return power.getTopItemCondition().test(level, top) &&
                power.getBottomItemCondition().test(level, bottom) &&
                power.getOutputItemCondition().test(level, original) &&
                (pos.isEmpty() || power.getBlockCondition().test(level, pos.get()));
    }

    public enum ResultType {
        UNCHANGED, SPECIFIED, FROM_TOP, FROM_BOTTOM
    }
}
