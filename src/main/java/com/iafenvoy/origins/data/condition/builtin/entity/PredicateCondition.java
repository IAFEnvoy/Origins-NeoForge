package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PredicateCondition(ResourceLocation predicate) implements EntityCondition {
    public static final MapCodec<PredicateCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("predicate").forGetter(PredicateCondition::predicate)
    ).apply(instance, PredicateCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (entity.level() instanceof ServerLevel level) {
            Holder.Reference<LootItemCondition> lootCondition = level.getServer().reloadableRegistries().lookup().get(Registries.PREDICATE, ResourceKey.create(Registries.PREDICATE, this.predicate)).orElse(null);
            if (lootCondition != null)
                return lootCondition.value().test(new LootContext.Builder(new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, entity.position())
                        .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                        .create(LootContextParamSets.COMMAND)).create(Optional.empty()));
        }
        return false;
    }
}
