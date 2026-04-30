package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.builtin.regular.InventoryPower;
import com.iafenvoy.origins.util.InventoryUtil;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record InventoryCondition(InventoryUtil.ProcessMode processMode, ItemCondition itemCondition, IntList slot,
                                 Optional<ResourceLocation> power, Comparison comparison) implements EntityCondition {
    public static final MapCodec<InventoryCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            InventoryUtil.ProcessMode.CODEC.optionalFieldOf("process_mode", InventoryUtil.ProcessMode.ITEMS).forGetter(InventoryCondition::processMode),
            ItemCondition.optionalCodec("item_condition").forGetter(InventoryCondition::itemCondition),
            CombinedCodecs.INT.optionalFieldOf("slot", IntList.of()).forGetter(InventoryCondition::slot),
            ResourceLocation.CODEC.optionalFieldOf("power").forGetter(InventoryCondition::power),
            Comparison.CODEC.forGetter(InventoryCondition::comparison)
    ).apply(i, InventoryCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        boolean result = false;
        int matches = 0;

        if (this.power.isEmpty()) {
            matches += InventoryUtil.checkInventory(this.itemCondition, this.slot, entity, null, this.processMode.getProcessor());
            result = this.comparison().compare(matches);
        } else {
            PowerContainer container = ApoliAPI.getPowerContainer(entity);
            if (container == null) {
                return result;
            }

            Holder<ConfiguredPower<?, ?>> targetPower = (Holder<ConfiguredPower<?, ?>>) (Object) container.getPower(configuration.power().get());
            if (targetPower == null || !targetPower.isBound() || !(targetPower.value().getFactory() instanceof InventoryPower)) {
                return result;
            }
            ConfiguredPower<io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration, InventoryPower> inventoryPower = (ConfiguredPower<io.github.edwinmindcraft.apoli.common.power.configuration.InventoryConfiguration, InventoryPower>) targetPower.value();

            matches += InventoryUtil.checkInventory(configuration.itemCondition(), configuration.slots(), entity, inventoryPower, configuration.processMode().getProcessor());
            result = configuration.comparison().check(matches);

        }

        return result;
    }
}
