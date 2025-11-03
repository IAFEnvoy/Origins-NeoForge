package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.EntitySetAttachment;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record ActionOnSetAction(ResourceLocation set, BiEntityAction biEntityAction,
                                BiEntityCondition biEntityCondition, int limit,
                                boolean reverse) implements EntityAction {
    public static final MapCodec<ActionOnSetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("set").forGetter(ActionOnSetAction::set),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(ActionOnSetAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ActionOnSetAction::biEntityCondition),
            Codec.INT.optionalFieldOf("limit", 0).forGetter(ActionOnSetAction::limit),
            Codec.BOOL.optionalFieldOf("reverse", false).forGetter(ActionOnSetAction::reverse)
    ).apply(i, ActionOnSetAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        if (!(source.level() instanceof ServerLevel serverLevel)) return;
        List<UUID> uuids = EntitySetAttachment.get(source).getEntityUuids(this.set);
        if (this.reverse) Collections.reverse(uuids);
        int remain = this.limit;
        for (UUID uuid : uuids) {
            if (remain <= 0) break;
            Entity entity = serverLevel.getEntity(uuid);
            if (entity == null || !this.biEntityCondition.test(source, entity)) continue;
            this.biEntityAction.execute(source, entity);
            remain--;
        }
    }
}
