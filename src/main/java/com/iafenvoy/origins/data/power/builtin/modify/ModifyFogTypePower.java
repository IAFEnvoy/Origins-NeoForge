package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.annotation.NotImplementedYet;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NotImplementedYet
public class ModifyFogTypePower extends Power {
    public static final MapCodec<ModifyFogTypePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            ExtraEnumCodecs.FOG_TYPE.fieldOf("to").forGetter(ModifyFogTypePower::getTo),
            ExtraEnumCodecs.FOG_TYPE.optionalFieldOf("from").forGetter(ModifyFogTypePower::getFrom)
    ).apply(i, ModifyFogTypePower::new));

    private final FogType to;
    private final Optional<FogType> from;

    public ModifyFogTypePower(BaseSettings settings, FogType to, Optional<FogType> from) {
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

    public Optional<FogType> tryReplace(Entity entity, FogType original) {
        if (this.getFrom().isEmpty())
            return Optional.of(this.getTo());
        return this.getFrom().filter(original::equals).map(k -> this.getTo());
    }
}
