package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyCameraSubmersionPower extends Power {
    public static final MapCodec<ModifyCameraSubmersionPower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ExtraEnumCodecs.FOG_TYPE.fieldOf("to").forGetter(ModifyCameraSubmersionPower::getTo),
            ExtraEnumCodecs.FOG_TYPE.optionalFieldOf("from").forGetter(ModifyCameraSubmersionPower::getFrom)
    ).apply(i, ModifyCameraSubmersionPower::new));

    private final FogType to;
    private final Optional<FogType> from;

    public ModifyCameraSubmersionPower(BaseSettings settings, FogType to, Optional<FogType> from) {
        super(settings);
        this.to = to;
        this.from = from;
    }

    public FogType getTo() {
        return this.to;
    }

    public Optional<FogType> getFrom() {
        return this.from;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }

    public static Optional<FogType> tryReplace(Entity entity, FogType original) {
        return OriginDataHolder.get(entity).streamActivePowers(ModifyCameraSubmersionPower.class).map(x -> x.from.isEmpty() ? Optional.of(x.to) : x.from.filter(original::equals).map(k -> x.to)).flatMap(Optional::stream).findFirst();
    }
}
