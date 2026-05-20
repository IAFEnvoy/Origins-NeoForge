package com.iafenvoy.origins.attachment;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.HolderHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public record PowerHolder(ResourceLocation id, Power power) {
    public PowerHolder(Holder<Power> holder) {
        this(HolderHelper.id(holder), holder.value());
    }
}
