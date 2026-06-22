package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.builtin.regular.ElytraFlightPower;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
        if (!(entity instanceof LivingEntity livingEntity))
            return false;
        boolean ability = true;
        // 26.1版本：Caelus的canFallFly()不可用：如果实体拥有
        // 激活的ElytraFlightPower（参见LivingEntityMixin#canGlide）或装备了真实的鞘翅。
        if (this.checkAbilities)
            ability = canElytraFly(livingEntity);
        boolean state = true;
        if (this.checkState)
            state = !livingEntity.onGround() && !livingEntity.isFallFlying() && !livingEntity.isInWater() && !livingEntity.hasEffect(MobEffects.LEVITATION);
        return ability && state;
    }

    private static boolean canElytraFly(LivingEntity entity) {
        if (OriginDataHolder.get(entity).hasActivePower(ElytraFlightPower.class))
            return true;
        for (EquipmentSlot slot : EquipmentSlot.VALUES)
            if (LivingEntity.canGlideUsing(entity.getItemBySlot(slot), slot))
                return true;
        return false;
    }
}
