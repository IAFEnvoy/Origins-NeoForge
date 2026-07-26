package com.iafenvoy.origins.data.action.builtin.bientity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record CopyOriginAction(Holder<Layer> layer, boolean modifyActor, boolean modifyTarget) implements BiEntityAction {
    public static final MapCodec<CopyOriginAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Layer.CODEC.fieldOf("layer").forGetter(CopyOriginAction::layer),
            Codec.BOOL.optionalFieldOf("modify_actor", false).forGetter(CopyOriginAction::modifyActor),
            Codec.BOOL.optionalFieldOf("modify_target", true).forGetter(CopyOriginAction::modifyTarget)
    ).apply(instance, CopyOriginAction::new));

    @Override
    public @NotNull MapCodec<? extends BiEntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity actor, @NotNull Entity target) {
        if (actor.level().isClientSide || (!this.modifyActor && !this.modifyTarget)) {
            return;
        }
        OriginDataHolder.optional(actor).ifPresent(actorData ->
                OriginDataHolder.optional(target).ifPresent(targetData -> {
                    Holder<Origin> actorOrigin = actorData.getOrigin(this.layer);
                    Holder<Origin> targetOrigin = targetData.getOrigin(this.layer);
                    if (this.modifyActor) {
                        actorData.setOrigin(this.layer, targetOrigin);
                        actorData.sync();
                    }
                    if (this.modifyTarget) {
                        targetData.setOrigin(this.layer, actorOrigin);
                        targetData.sync();
                    }
                }));
    }
}
