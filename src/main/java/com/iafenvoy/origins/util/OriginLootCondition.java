package com.iafenvoy.origins.util;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OriginLootCondition implements LootItemCondition {

    public static final MapCodec<OriginLootCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("origin").forGetter(OriginLootCondition::getOrigin),
            ResourceLocation.CODEC.optionalFieldOf("layer").forGetter(OriginLootCondition::getLayer)
    ).apply(i, OriginLootCondition::new));
    public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

    private final ResourceLocation origin;
    private final Optional<ResourceLocation> layer;

    private OriginLootCondition(ResourceLocation origin, Optional<ResourceLocation> layer) {
        this.origin = origin;
        this.layer = layer;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) {
            return false;
        }

        EntityOriginAttachment attachment = EntityOriginAttachment.get(entity);
        for (Map.Entry<Holder<Layer>, Holder<Origin>> entry : attachment.getOrigins().entrySet()) {
            ResourceLocation layerId = RLHelper.id(entry.getKey());
            ResourceLocation originId = RLHelper.id(entry.getValue());

            if (this.layer.map(layerId::equals).orElse(true) && originId.equals(this.origin)) {
                return true;
            }
        }

        return false;
    }

    public ResourceLocation getOrigin() {
        return this.origin;
    }

    public Optional<ResourceLocation> getLayer() {
        return this.layer;
    }
}
