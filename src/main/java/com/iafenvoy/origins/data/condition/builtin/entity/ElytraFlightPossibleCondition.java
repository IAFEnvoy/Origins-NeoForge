package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.event.common.CanFlyWithoutElytraEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public record ElytraFlightPossibleCondition(boolean checkState, boolean checkAbilities) implements EntityCondition {
    public static final MapCodec<ElytraFlightPossibleCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("check_state", false).forGetter(ElytraFlightPossibleCondition::checkState),
            Codec.BOOL.optionalFieldOf("check_abilities", true).forGetter(ElytraFlightPossibleCondition::checkAbilities)
    ).apply(i, ElytraFlightPossibleCondition::new));

    @Override
    public @NotNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull Entity entity) {
        //FIXME::Correct?
        if (!(entity instanceof LivingEntity living)) return false;
        boolean state = true, ability = true, checked = false;
        if (this.checkState) {
            checked = true;
            state = !living.onGround() && !living.isFallFlying() && !living.isInWater() && !living.hasEffect(MobEffects.LEVITATION);
        }
        if (this.checkAbilities) {
            checked = true;
            ItemStack equippedChestStack = living.getItemBySlot(EquipmentSlot.CHEST);
            ability = equippedChestStack.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(equippedChestStack) || NeoForge.EVENT_BUS.post(new CanFlyWithoutElytraEvent(living)).getResult().allow();
        }
        return checked && state && ability;
    }
}
