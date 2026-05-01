package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.illusivesoulworks.caelus.api.CaelusApi;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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
        boolean ability = this.checkAbilities || CaelusApi.getInstance().canFallFly(livingEntity, false);
        //FORGE STILL DOESN'T HAVE ELYTRA EVENTS.
        //(Also PRs are for 1.18.2 and that ain't gonna happen for quite a while)
        //I'm just going to assume that Caelus does all the hard work for me.
        //For when forge gets events (if ever):
		/*
		if (!ability && EntityElytraEvents.CUSTOM.invoker().useCustomElytra(livingEntity, false))
			ability = true;
		if (!EntityElytraEvents.ALLOW.invoker().allowElytraFlight(livingEntity))
			ability = false;
		*/
        boolean state = true;
        if (this.checkState)
            state = !livingEntity.onGround() && !livingEntity.isFallFlying() && !livingEntity.isInWater() && !livingEntity.hasEffect(MobEffects.LEVITATION);
        return ability && state;
    }
}
