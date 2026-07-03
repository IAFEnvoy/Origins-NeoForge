package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data._common.helper.InventoryConditionHelper;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.builtin.regular.InventoryPower;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record InventoryCondition(ProcessMode processMode, ItemCondition itemCondition, IntList slot,
                                 Optional<PowerReference> power,
                                 Comparison comparison) implements EntityCondition, InventoryConditionHelper {
    public static final MapCodec<InventoryCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ProcessMode.CODEC.optionalFieldOf("process_mode", ProcessMode.ITEMS).forGetter(InventoryCondition::processMode),
            ItemCondition.optionalCodec("item_condition").forGetter(InventoryCondition::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(InventoryCondition::slot),
            PowerReference.CODEC.optionalFieldOf("power").forGetter(InventoryCondition::power),
            Comparison.CODEC.forGetter(InventoryCondition::comparison)
    ).apply(i, InventoryCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        Optional<PowerHolder> power = this.power.flatMap(x -> x.get(entity.registryAccess()));
        if (power.isPresent() && !(power.get().power() instanceof InventoryPower)) return false;
        return this.comparison().compare(this.checkInventory(entity, this.processMode.getProcessor()));
    }
}
