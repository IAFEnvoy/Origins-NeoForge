package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.common.RaycastSettings;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

//TODO::Space, Direction, Entity Distance, Block Distance. https://origins.readthedocs.io/en/latest/types/entity_condition_types/raycast/
public record RaycastCondition(RaycastSettings settings, BiEntityCondition matchCondition,
                               BiEntityCondition hitCondition,
                               BlockCondition blockCondition) implements EntityCondition {
    public static final MapCodec<RaycastCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RaycastSettings.CODEC.forGetter(RaycastCondition::settings),
            BiEntityCondition.optionalCodec("match_bientity_condition").forGetter(RaycastCondition::matchCondition),
            BiEntityCondition.optionalCodec("hit_bientity_condition").forGetter(RaycastCondition::hitCondition),
            BlockCondition.optionalCodec("block_condition").forGetter(RaycastCondition::blockCondition)
    ).apply(instance, RaycastCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        HitResult hitResult = this.settings().perform(entity, this.matchCondition());
        if (hitResult.getType() == HitResult.Type.MISS) return false;
        if (hitResult instanceof BlockHitResult bhr)
            return this.blockCondition().test(entity.level(), bhr.getBlockPos());
        if (hitResult instanceof EntityHitResult ehr)
            return this.hitCondition().test(entity, ehr.getEntity());
        return true;
    }
}
