package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.helper.InventoryActionHelper;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ModifyInventoryAction(EntityAction entityAction, ItemAction itemAction, ItemCondition itemCondition,
                                    IntList slot, Optional<Holder<Power>> power, ProcessMode processMode,
                                    int limit) implements EntityAction, InventoryActionHelper {
    public static final MapCodec<ModifyInventoryAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action").forGetter(ModifyInventoryAction::entityAction),
            ItemAction.CODEC.fieldOf("item_action").forGetter(ModifyInventoryAction::itemAction),
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyInventoryAction::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(ModifyInventoryAction::slot),
            Power.CODEC.optionalFieldOf("power").forGetter(ModifyInventoryAction::power),
            ProcessMode.CODEC.optionalFieldOf("process_mode", ProcessMode.STACKS).forGetter(ModifyInventoryAction::processMode),
            Codec.INT.optionalFieldOf("limit", 0).forGetter(ModifyInventoryAction::limit)
    ).apply(i, ModifyInventoryAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        this.modifyInventory(source, this.processMode.getProcessor(), this.limit);
    }
}
