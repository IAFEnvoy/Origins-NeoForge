package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ModifyGrindstonePower(ItemCondition topItemCondition, ItemCondition bottomItemCondition, ItemCondition outputItemCondition,
                                    BlockCondition blockCondition, Optional<ItemStack> resultStack, ItemAction resultItemAction, ItemAction lateItemAction,
                                    EntityAction entityAction, BlockAction blockAction,ResultType resultType) implements Power {

    public static final MapCodec<ModifyGrindstonePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("top_condition").forGetter(ModifyGrindstonePower::topItemCondition),
            ItemCondition.optionalCodec("bottom_condition").forGetter(ModifyGrindstonePower::bottomItemCondition),
            ItemCondition.optionalCodec("output_condition").forGetter(ModifyGrindstonePower::outputItemCondition),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifyGrindstonePower::blockCondition),
            ItemStack.CODEC.optionalFieldOf("result_stack").forGetter( p -> p.resultStack),
            ItemAction.optionalCodec("item_action").forGetter(ModifyGrindstonePower::resultItemAction),
            ItemAction.optionalCodec("item_action_after_grinding").forGetter(ModifyGrindstonePower::lateItemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyGrindstonePower::entityAction),
            BlockAction.optionalCodec("block_action").forGetter(ModifyGrindstonePower::blockAction),
            ExtraEnumCodecs.enumCodec(ResultType::valueOf).optionalFieldOf("result_type", ResultType.UNCHANGED).forGetter(ModifyGrindstonePower::resultType)
    ).apply(i, ModifyGrindstonePower::new));

// TODO ConfiguredModifier

//    public static final Codec<ModifyGrindstoneConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ConfiguredItemCondition.optional("top_condition").forGetter(ModifyGrindstoneConfiguration::topItemCondition),
//            ConfiguredItemCondition.optional("bottom_condition").forGetter(ModifyGrindstoneConfiguration::bottomItemCondition),
//            ConfiguredItemCondition.optional("output_condition").forGetter(ModifyGrindstoneConfiguration::outputItemCondition),
//            ConfiguredBlockCondition.optional("block_condition").forGetter(ModifyGrindstoneConfiguration::blockCondition),
//            ExtraCodecs.strictOptionalField(SerializableDataTypes.ITEM_STACK, "result_stack").forGetter(ModifyGrindstoneConfiguration::resultStack),
//            ConfiguredItemAction.optional("item_action").forGetter(ModifyGrindstoneConfiguration::resultItemAction),
//            ConfiguredItemAction.optional("item_action_after_grinding").forGetter(ModifyGrindstoneConfiguration::lateItemAction),
//            ConfiguredEntityAction.optional("entity_action").forGetter(ModifyGrindstoneConfiguration::entityAction),
//            ConfiguredBlockAction.optional("block_action").forGetter(ModifyGrindstoneConfiguration::blockAction),
//            ExtraCodecs.strictOptionalField(SerializableDataType.enumValue(ModifyGrindstonePower.ResultType.class), "result_type", ModifyGrindstonePower.ResultType.UNCHANGED).forGetter(ModifyGrindstoneConfiguration::resultType),
//            ExtraCodecs.strictOptionalField(ConfiguredModifier.CODEC, "xp_modifier").forGetter(ModifyGrindstoneConfiguration::experienceModifier)
//    ).apply(instance, ModifyGrindstoneConfiguration::new));
    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public void tryExecute(ModifyGrindstonePower power, Entity entity, ItemStack itemStack, Optional<BlockPos> pos) {
        power.lateItemAction().execute(entity.level(),entity, itemStack);
        power.entityAction().execute(entity);
        pos.ifPresent(blockPos -> power.blockAction().execute(entity.level(), blockPos, Direction.UP));
    }

    public boolean doesApply(ModifyGrindstonePower power, Level level, ItemStack top, ItemStack bottom, ItemStack original, Optional<BlockPos> pos) {
        return power.topItemCondition().test(level, top) &&
                power.bottomItemCondition().test(level, bottom) &&
                power.outputItemCondition().test(level, original) &&
                (pos.isEmpty() || power.blockCondition().test(level, pos.get()));
    }

    public enum ResultType {
        UNCHANGED, SPECIFIED, FROM_TOP, FROM_BOTTOM
    }
}
