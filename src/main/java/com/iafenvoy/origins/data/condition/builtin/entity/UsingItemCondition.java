package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record UsingItemCondition(ItemCondition itemCondition) implements EntityCondition {
    public static final MapCodec<UsingItemCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.CODEC.fieldOf("item_condition").forGetter(UsingItemCondition::itemCondition)
    ).apply(i, UsingItemCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        if (entity instanceof LivingEntity living && living.isUsingItem()) {
            InteractionHand hand = living.getUsedItemHand();
            ItemStack stack = living.getItemInHand(hand);
            return this.itemCondition.test(living.level(), stack);
        } else return false;
    }
}
