package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record SetSizeCondition(Identifier set, Comparison comparison) implements EntityCondition {
    public static final MapCodec<SetSizeCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WildcardCodec.INSTANCE.fieldOf("set").forGetter(SetSizeCondition::set),
            Comparison.CODEC.forGetter(SetSizeCondition::comparison)
    ).apply(i, SetSizeCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        return this.comparison.compare(OriginDataHolder.get(entity).getComponentHolder(this.set, EntitySetComponent.class).map(EntitySetComponent.SetHolder::getSize).orElse(0));
    }
}
