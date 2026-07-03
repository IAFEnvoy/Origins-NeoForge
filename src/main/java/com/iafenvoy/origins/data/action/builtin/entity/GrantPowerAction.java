package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.WildcardCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record GrantPowerAction(Holder<Power> power, ResourceLocation source) implements EntityAction {
    public static final MapCodec<GrantPowerAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Power.CODEC.fieldOf("power").forGetter(GrantPowerAction::power),
            WildcardCodec.INSTANCE.fieldOf("source").forGetter(GrantPowerAction::source)
    ).apply(i, GrantPowerAction::new));

    @Override
    public @NotNull MapCodec<? extends EntityAction> codec() {
        return CODEC;
    }

    @Override
    public void execute(@NotNull Entity source) {
        OriginDataHolder.optional(source).ifPresent(h -> h.grantPower(this.source, this.power));
    }
}
