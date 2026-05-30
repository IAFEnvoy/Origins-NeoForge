package com.iafenvoy.origins.data.power.reference;

import com.iafenvoy.origins.data.power.MultiplePower;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.HolderHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public record PowerHolder(ResourceLocation id, Power power) {
    public PowerHolder(Holder<Power> holder) {
        this(HolderHelper.id(holder), holder.value());
    }

    public Stream<PowerHolder> stream() {
        return this.power instanceof MultiplePower multiple ? multiple.getPowers().entrySet().stream().flatMap(x -> new PowerHolder(this.id.withSuffix("_" + x.getKey()), x.getValue()).stream()) : Stream.of(this);
    }

    public MutableComponent getName() {
        return this.power.getName(this.id);
    }
}
