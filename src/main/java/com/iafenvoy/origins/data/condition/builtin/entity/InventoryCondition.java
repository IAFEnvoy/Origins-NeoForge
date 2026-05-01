package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data._common.helper.InventoryConditionHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.regular.InventoryPower;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record InventoryCondition(ProcessMode processMode, ItemCondition itemCondition, IntList slot,
                                 Optional<Holder<Power>> power,
                                 Comparison comparison) implements EntityCondition, InventoryConditionHelper {
    public static final MapCodec<InventoryCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ProcessMode.CODEC.optionalFieldOf("process_mode", ProcessMode.ITEMS).forGetter(InventoryCondition::processMode),
            ItemCondition.optionalCodec("item_condition").forGetter(InventoryCondition::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(InventoryCondition::slot),
            Power.CODEC.optionalFieldOf("power").forGetter(InventoryCondition::power),
            Comparison.CODEC.forGetter(InventoryCondition::comparison)
    ).apply(i, InventoryCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        int matches = 0;
        if (this.power.isEmpty())
            matches += this.checkInventory(entity, this.processMode.getProcessor());
        else {
            Holder<Power> targetPower = this.power.get();
            if (!(targetPower.value() instanceof InventoryPower)) return false;
            matches += this.checkInventory(entity, this.processMode.getProcessor());
        }
        return this.comparison().compare(matches);
    }
}
