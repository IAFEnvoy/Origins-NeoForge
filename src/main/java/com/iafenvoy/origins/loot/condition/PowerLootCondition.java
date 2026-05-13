package com.iafenvoy.origins.loot.condition;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsLootItemConditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PowerLootCondition(LootContext.EntityTarget target, Holder<Power> power,
                                 Optional<ResourceLocation> source) implements LootItemCondition {
    public static final MapCodec<PowerLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("entity", LootContext.EntityTarget.THIS).forGetter(PowerLootCondition::target),
            Power.CODEC.fieldOf("power").forGetter(PowerLootCondition::power),
            ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::source)
    ).apply(instance, PowerLootCondition::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return OriginsLootItemConditions.POWER.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return OriginDataHolder.get(lootContext.getParamOrNull(this.target.getParam())).hasPower(this.power);
    }
}
