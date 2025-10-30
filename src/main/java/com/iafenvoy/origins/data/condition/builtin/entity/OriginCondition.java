package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.EntityOriginAttachment;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public record OriginCondition(Holder<Origin> origin, Optional<Holder<Layer>> layer) implements EntityCondition {
    public static final MapCodec<OriginCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Origin.CODEC.fieldOf("origin").forGetter(OriginCondition::origin),
            Layer.CODEC.optionalFieldOf("layer").forGetter(OriginCondition::layer)
    ).apply(i, OriginCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        //TODO::More APIs in attachment?
        Map<Holder<Layer>, Holder<Origin>> map = EntityOriginAttachment.get(entity).getOriginsView();
        return map.containsValue(this.origin) && this.layer.map(map::containsKey).orElse(true);
    }
}
