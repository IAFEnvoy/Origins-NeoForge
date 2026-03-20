package com.iafenvoy.origins.util;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.origin.Origin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChoseOriginCriterion extends SimpleCriterionTrigger<ChoseOriginCriterion.TriggerInstance> {

    public static final ChoseOriginCriterion INSTANCE = new ChoseOriginCriterion();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "chose_origin");

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Holder<Origin> origin) {
        ResourceLocation originId = RLHelper.id(origin);
        this.trigger(player, instance -> instance.matches(originId));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  ResourceLocation originId) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ResourceLocation.CODEC.fieldOf("origin").forGetter(TriggerInstance::originId)
        ).apply(i, TriggerInstance::new));

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return this.player;
        }

        public boolean matches(ResourceLocation id) {
            return this.originId.equals(id);
        }
    }
}
