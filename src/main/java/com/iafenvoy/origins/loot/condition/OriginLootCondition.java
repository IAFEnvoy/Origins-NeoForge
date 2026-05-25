package com.iafenvoy.origins.loot.condition;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.registry.OriginsLootItemConditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record OriginLootCondition(LootContext.EntityTarget target, Holder<Origin> origin,
                                  Optional<Holder<Layer>> layer) implements LootItemCondition {
    public static final MapCodec<OriginLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("entity", LootContext.EntityTarget.THIS).forGetter(OriginLootCondition::target),
            Origin.CODEC.fieldOf("origin").forGetter(OriginLootCondition::origin),
            Layer.CODEC.optionalFieldOf("layer").forGetter(OriginLootCondition::layer)
    ).apply(instance, OriginLootCondition::new));

    @Override
    public @NotNull LootItemConditionType getType() {
        return OriginsLootItemConditions.ORIGIN.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(this.target.getParam());
        if (entity == null) return false;
        OriginDataHolder holder = OriginDataHolder.get(entity);
        return this.layer.map(l -> holder.hasOrigin(l, this.origin)).orElseGet(() -> holder.hasOrigin(this.origin));
    }
}
