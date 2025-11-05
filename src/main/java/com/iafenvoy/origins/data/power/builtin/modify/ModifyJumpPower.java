package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record ModifyJumpPower(EntityAction entityAction) implements Power {

    public static final MapCodec<ModifyJumpPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.optionalCodec("entity_action").forGetter(ModifyJumpPower::entityAction)
    ).apply(i, ModifyJumpPower::new));

// TODO ListConfiguration

//    public static final Codec<ModifyJumpConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            ListConfiguration.MODIFIER_CODEC.forGetter(ModifyJumpConfiguration::modifiers),
//            ConfiguredEntityAction.optional("entity_action").forGetter(ModifyJumpConfiguration::condition)
//    ).apply(instance, ModifyJumpConfiguration::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


//    public static double apply(Entity player, double baseValue) {
//        return PowerContainer.modify(player, ApoliPowers.MODIFY_JUMP.get(), baseValue, x -> true, x -> x.value().getFactory().execute(x.value(), player));
//    }

    public void execute(Entity player) {
        entityAction().execute(player);
    }

}
