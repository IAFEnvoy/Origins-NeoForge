package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ModifyFogTypePower(FogType to,Optional<FogType> from) implements Power {


    public static final MapCodec<ModifyFogTypePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraEnumCodecs.FOG_TYPE.fieldOf("to").forGetter(ModifyFogTypePower::to),
            ExtraEnumCodecs.FOG_TYPE.optionalFieldOf("from").forGetter(ModifyFogTypePower::from)
    ).apply(i, ModifyFogTypePower::new));

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

//    public static Optional<FogType> tryReplace(Entity entity, FogType original) {
//        return PowerContainer.getPowers(entity, MODIFY_CAMERA_SUBMERSION.get()).stream().flatMap(x -> x.value().getFactory().tryReplace(x.value(), entity, original).stream()).findFirst();
//    }

    public Optional<FogType> tryReplace(Entity entity, FogType original) {
        if (from().isEmpty())
            return Optional.of(to());
        return from().filter(original::equals).map(k -> to());
    }
}
